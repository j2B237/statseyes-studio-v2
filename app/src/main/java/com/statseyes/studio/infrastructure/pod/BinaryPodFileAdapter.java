// infrastructure/pod/BinaryPodFileAdapter.java
package com.statseyes.studio.infrastructure.pod;

import com.statseyes.studio.application.port.PodFileImportPort;
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

@Component
public class BinaryPodFileAdapter implements PodFileImportPort {

    private static final String MAGIC = "PODL";
    private static final int HEADER_SIZE = 512;
    private static final int BLOCK_SIZE = 512;
    private static final int RECORD_SIZE = 40;
    private static final int RECORDS_PER_BLOCK = BLOCK_SIZE / RECORD_SIZE;

    private static final int RECORD_TYPE_EMPTY = 0;
    private static final int RECORD_TYPE_SAMPLE = 1;
    private static final int RECORD_TYPE_SESSION_START = 2;
    private static final int RECORD_TYPE_SESSION_END = 3;

    private static final int MAX_LATITUDE_E7  = 900_000_000;
    private static final int MAX_LONGITUDE_E7 = 1_800_000_000;

    @Override
    public List<PodSessionData> parse(InputStream binaryStream) {
        try {
            long nextFreeBlock = readHeader(binaryStream);
            return readSessions(binaryStream, nextFreeBlock);
        } catch (IOException e) {
            throw new PodFileFormatException("Erreur de lecture du flux pod", e);
        }
    }

    private long readHeader(InputStream in) throws IOException {
        byte[] raw = in.readNBytes(HEADER_SIZE);
        if (raw.length < HEADER_SIZE) {
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

    private List<PodSessionData> readSessions(InputStream in, long nextFreeBlock) throws IOException {

        List<PodSessionData> sessions = new ArrayList<>();
        List<GpsData> currentSamples = null;
        long currentSessionNumber = -1;

        for (long blockIndex = 1; blockIndex < nextFreeBlock; blockIndex++) {
            byte[] raw = in.readNBytes(BLOCK_SIZE);
            if (raw.length < BLOCK_SIZE) break; // flux coupe avant la fin annoncee -- ici, arret global legitime

            ByteBuffer block = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < RECORDS_PER_BLOCK; i++) {
                int offset = i * RECORD_SIZE;
                int recordType = block.get(offset) & 0xFF;

                switch (recordType) {
                    case RECORD_TYPE_EMPTY -> {
                        // Fin du flux utile DANS CE BLOC uniquement (voir protocole.txt) --
                        // on passe au bloc suivant, on n'arrete pas tout le fichier :
                        // une session peut se terminer en milieu de bloc pendant qu'une
                        // autre redemarre proprement au bloc suivant.
                    }

                    case RECORD_TYPE_SESSION_START -> {
                        currentSessionNumber = Integer.toUnsignedLong(block.getInt(offset + 4));
                        currentSamples = new ArrayList<>();
                    }

                    case RECORD_TYPE_SESSION_END -> {
                        if (currentSamples != null) {
                            sessions.add(new PodSessionData(currentSessionNumber, currentSamples));
                        }
                        currentSamples = null;
                    }

                    case RECORD_TYPE_SAMPLE -> {
                        if (currentSamples != null) {
                            currentSamples.add(parseSampleRecord(block, offset));
                        }
                    }

                    default -> throw new PodFileFormatException(
                            "Record_Type inconnu (" + recordType + ") -- flux corrompu ou version non geree."
                    );
                }

                if (recordType == RECORD_TYPE_EMPTY) break; // sort de la boucle des enregistrements, pas des blocs
            }
        }

        if (currentSamples != null && !currentSamples.isEmpty()) {
            sessions.add(new PodSessionData(currentSessionNumber, currentSamples));
        }
        return sessions;
    }

    private GpsData parseSampleRecord(ByteBuffer block, int offset) {
        int timeMs    = block.getInt(offset + 8);
        int latitude  = block.getInt(offset + 12);
        int longitude = block.getInt(offset + 16);
        int speed     = block.getInt(offset + 20);
        int course    = block.getShort(offset + 24);

        boolean sane = Math.abs(latitude) <= MAX_LATITUDE_E7 && Math.abs(longitude) <= MAX_LONGITUDE_E7;
        return new GpsData(latitude, longitude, speed, timeMs, course, sane);
    }
}