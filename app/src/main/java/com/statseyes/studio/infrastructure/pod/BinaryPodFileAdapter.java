package com.statseyes.studio.infrastructure.pod;

import com.statseyes.studio.application.port.PodFileImportPort;
import com.statseyes.studio.domain.exception.PodFileFormatException;
import com.statseyes.studio.domain.model.GpsData;
import com.statseyes.studio.domain.model.PodSessionData;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class BinaryPodFileAdapter implements PodFileImportPort{

    private static final String MAGIC = "PODL";
    private static final int HEADER_SIZE = 512;
    private static final int BLOCK_SIZE = 512;
    private static final int RECORD_SIZE = 40;
    private static final int RECORDS_PER_BLOCK = BLOCK_SIZE / RECORD_SIZE;

    private static final int RECORD_TYPE_EMPTY = 0;
    private static final int RECORD_TYPE_SAMPLE = 1;
    private static final int RECORD_TYPE_SESSION_START = 2;
    private static final int RECORD_TYPE_SESSION_END = 3;

    /**
     * Garde-fou anti-corruption SD -- PAS un indicateur de qualite de fix
     * GPS : le firmware ne loggue jamais un Record_Type=1 issu d'une trame
     * $GPRMC invalide (voir fsm-utils.adb : Log_Sample n'est appele que si
     * Parsing_Result = GPS_Ok). Un point hors bornes physiques ne peut donc
     * venir que d'une corruption carte, jamais d'un "pas de fix".
     * @param binaryFilePath
     * @return
     */
    private static final int MAX_LATITUDE_E7  = 900_000_000;
    private static final int MAX_LONGITUDE_E7 = 1_800_000_000;

    @Override
    public List<PodSessionData> parse(Path binaryFilePath){
        try (FileChannel channel = FileChannel.open(binaryFilePath, StandardOpenOption.READ)
        ){
            PodFileHeader header = readHeader(channel);
            return readSessions(channel, header);
        } catch (IOException e) {
            throw new PodFileFormatException(
                    "Impossible de lire le fichier pod : " + binaryFilePath, e
            );
        }
    }

    private PodFileHeader readHeader(FileChannel channel) throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        if(!readFully(channel, buffer, 0)){
            throw new PodFileFormatException("Fichier pod tronque : en-tete incomplet");
        }

        buffer.flip();

        byte[] magicBytes = new byte[4];
        buffer.get(magicBytes);

        String magic = new String(magicBytes, StandardCharsets.US_ASCII);
        if (!MAGIC.equals(magic)) {
            throw new PodFileFormatException(
                    "En-tete invalide : magic attendu 'PODL', trouve '" + magic + "'. " +
                            "Si ce fichier vient d'un dump USB ('Get metrics'), il commence par un " +
                            "marqueur 0xAA55AA55 + une taille avant le bloc 0 -- ce parseur attend " +
                            "un dump SD brut commencant directement au bloc 0."
            );
        }

        buffer.position(8);
        long nextFreeBlock = Integer.toUnsignedLong(buffer.getInt());
        long sessionCount  = Integer.toUnsignedLong(buffer.getInt());
        return new PodFileHeader(nextFreeBlock, sessionCount);
    }


    private List<PodSessionData> readSessions(
            FileChannel channel,
            PodFileHeader header
    ) throws IOException {

        List<PodSessionData> sessions = new ArrayList<>();
        List<GpsData> currentSamples = null;

        long currentSessionNumber = -1;

        blockLoop:
        for (long blockIndex = 1; blockIndex < header.nextFreeBlock(); blockIndex++) {

            ByteBuffer block = ByteBuffer.allocate(BLOCK_SIZE).order(ByteOrder.LITTLE_ENDIAN);
            if (!readFully(channel, block, blockIndex * BLOCK_SIZE)) break;

            block.flip();

            for (int i = 0; i < RECORDS_PER_BLOCK; i++) {

                int offset = i * RECORD_SIZE;
                int recordType = block.get(offset) & 0xFF;

                switch (recordType) {
                    case RECORD_TYPE_EMPTY -> { break blockLoop; }

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
                        // Echantillon hors session (avant tout Start_Session) :
                        // ignore plutot que de faire planter tout l'import.
                    }

                    default -> throw new PodFileFormatException(
                            "Record_Type inconnu (" + recordType + ") au bloc " + blockIndex +
                                    " -- fichier corrompu ou version de protocole non geree."
                    );
                }
            }
        }
        // Session encore ouverte en fin de fichier (pas de Record_Type=3) :
        // on recupere quand meme les donnees plutot que de les perdre.
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

        boolean sane = Math.abs(latitude) <= MAX_LATITUDE_E7
                && Math.abs(longitude) <= MAX_LONGITUDE_E7;

        return new GpsData(latitude, longitude, speed, timeMs, course, sane);
    }

    private boolean readFully(FileChannel channel, ByteBuffer buffer, long position) throws IOException {

        long pos = position;
        while (buffer.hasRemaining()) {
            int read = channel.read(buffer, pos);
            if (read == -1) return false;
            pos += read;
        }
        return true;
    }

    private record PodFileHeader(long nextFreeBlock, long sessionCount) {}
}
