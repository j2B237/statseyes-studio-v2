package com.statseyes.studio.infrastructure.pod;

import com.statseyes.studio.application.port.PodFileImportPort;
import com.statseyes.studio.domain.config.PodImportConfig;
import com.statseyes.studio.domain.exception.PodFileFormatException;
import com.statseyes.studio.domain.model.GpsData;
import com.statseyes.studio.domain.model.PodSessionData;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.statseyes.studio.domain.config.PodImportConfig.RECORD_SIZE;
import static com.sun.marlin.MarlinConst.BLOCK_SIZE;

@Component
public class BinaryPodFileAdapter implements PodFileImportPort {

    private static final String MAGIC = "PODL";

    private static final int RECORDS_PER_BLOCK =
            PodImportConfig.BLOCK_SIZE.getValue() / RECORD_SIZE.getValue();

    private static final int RECORD_TYPE_EMPTY = 0;
    private static final int RECORD_TYPE_SAMPLE = 1;
    private static final int RECORD_TYPE_SESSION_START = 2;
    private static final int RECORD_TYPE_SESSION_END = 3;

    @Override
    public List<PodSessionData> parse(InputStream binaryStream) {
        List<PodSessionData> sessions = new ArrayList<>();
        scan(binaryStream, sessions::add);
        return sessions;
    }

    @Override
    public Optional<PodSessionData> parseLastSession(InputStream binaryStream){
        // Chaque session terminée remplace la précédente : une seule reste en memoire
        AtomicReference<PodSessionData> last = new AtomicReference<>();
        scan(binaryStream, session ->
            { if (session.hasUsableData()) last.set(session);
        });
        return Optional.ofNullable(last.get());
    }

    private void scan(InputStream in, Consumer<PodSessionData> onSession ){
        try{
            long nextFreeBlock = readHeader(in);
            readSessions(in, nextFreeBlock, onSession);
        } catch (IOException e){
            throw new PodFileFormatException("Erreur de lecture du flux pod", e);
        }
    }
    private long readHeader(InputStream in) throws IOException {
        byte[] raw = in.readNBytes(PodImportConfig.HEADER_SIZE.getValue());
        if (raw.length < PodImportConfig.HEADER_SIZE.getValue()) {
            throw new PodFileFormatException("Flux tronque : en-tete incomplet");
        }
        ByteBuffer buffer = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);

        byte[] magicBytes = new byte[4];
        buffer.get(magicBytes);
        String magic = new String(magicBytes, StandardCharsets.US_ASCII);
        if (!MAGIC.equals(magic)) {
            throw new PodFileFormatException("En-tete invalide : magic attendu 'PODL', trouve '" + magic + "'");
        }

        buffer.position(8);
        return Integer.toUnsignedLong(buffer.getInt()); // Next_Free_Block
    }
    private void readSessions(InputStream in, long nextFreeBlock, Consumer<PodSessionData> onSession) throws IOException {
        List<GpsData> currentSamples = null;
        long currentSessionNumber = -1;

        for (long blockIndex = 1; blockIndex < nextFreeBlock; blockIndex++) {

            byte[] raw = in.readNBytes(PodImportConfig.BLOCK_SIZE.getValue());

            // flux coupé avant la fin annoncée
            if (raw.length < PodImportConfig.BLOCK_SIZE.getValue()) break;
            ByteBuffer block = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < RECORDS_PER_BLOCK; i++) {

                int offset = i * RECORD_SIZE.getValue();
                int recordType = block.get(offset) & 0xFF;

                // fin utile de CE bloc, pas du fichier
                if (recordType == RECORD_TYPE_EMPTY) break;

                switch (recordType) {

                    case RECORD_TYPE_SESSION_START -> {
                        // Session précédente jamais fermée (coupure d'alimentation) : on la rend quand même
                        if (currentSamples != null) {
                            onSession.accept(new PodSessionData(currentSessionNumber, currentSamples));
                        }
                        currentSessionNumber = Integer.toUnsignedLong(block.getInt(offset + 4));
                        currentSamples = new ArrayList<>();
                    }

                    case RECORD_TYPE_SESSION_END -> {

                        if (currentSamples != null) {
                            onSession.accept(new PodSessionData(currentSessionNumber, currentSamples));
                        }
                        currentSamples = null;
                    }

                    case RECORD_TYPE_SAMPLE -> {
                        if (currentSamples != null) currentSamples.add(parseSampleRecord(block, offset));
                    }

                    default -> throw new PodFileFormatException(
                            "Record_Type inconnu (" + recordType + ") : flux corrompu ou version non gérée.");
                }
            }
        }

        // Session encore ouverte en fin de flux : on récupère ses données
        if (currentSamples != null && !currentSamples.isEmpty()) {
            onSession.accept(new PodSessionData(currentSessionNumber, currentSamples));
        }
    }

    private GpsData parseSampleRecord(ByteBuffer block, int offset) {

        int timeMs    = block.getInt(offset + 8);
        int latitude  = block.getInt(offset + 12);
        int longitude = block.getInt(offset + 16);
        int speed     = block.getInt(offset + 20);
        int course    = block.getShort(offset + 24);

        boolean sane =
                Math.abs(latitude) <= PodImportConfig.MAX_LATITUDE_E7.getValue()
                        && Math.abs(longitude) <= PodImportConfig.MAX_LONGITUDE_E7.getValue();
        return new GpsData(latitude, longitude, speed, timeMs, course, sane);
    }
}