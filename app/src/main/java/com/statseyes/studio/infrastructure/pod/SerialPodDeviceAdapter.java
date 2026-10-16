package com.statseyes.studio.infrastructure.pod;

import com.fazecast.jSerialComm.SerialPort;
import com.statseyes.studio.application.port.PodDevicePort;

import com.statseyes.studio.domain.model.PodDevice;
import com.statseyes.studio.domain.model.PodDump;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class SerialPodDeviceAdapter implements PodDevicePort {

    // ====================
    // INSTANCE VARIABLES
    // ====================

    private static final int BAUD_RATE = 9600;
    private static final int BLOCK_SIZE = 512;
    private static final int READ_TIMEOUT_MS = 5000;
    private static final long BOOT_DELAY_MS = 2500;
    private static final int MAX_ATTEMPTS = 3;
    private static final int[] MARKER = {0xAA, 0x55, 0xAA, 0x55};
    private static final byte[] COMMAND = "Get metrics\n".getBytes(StandardCharsets.US_ASCII);


    @Override
    public List<PodDevice> listDevices() {
        return Arrays.stream(SerialPort.getCommPorts())
                .map(p -> new PodDevice(
                        p.getSystemPortName(),
                        p.getSystemPortName() + " : " + p.getDescriptivePortName(),
                        looksLikeUsb(p)))
                .toList();
    }

    @Override
    public PodDump openDump(String deviceId) throws IOException {

        SerialPort port = Arrays.stream(SerialPort.getCommPorts())
                .filter(p -> p.getSystemPortName().equals(deviceId))
                .findFirst()
                .orElseThrow(
                        () -> new IOException(
                                "Périphérique introuvable : " + deviceId +
                                ". Rebranchez le pod puis actualisez."
                        ));

        port.setBaudRate(BAUD_RATE);
        port.setNumDataBits(8);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setParity(SerialPort.NO_PARITY);
        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        port.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING,
                READ_TIMEOUT_MS,
                0
        );

        if (!port.openPort()) {
            throw new IOException("Impossible d'ouvrir " + deviceId +
                    ". Un autre programme l'utilise peut-être ; " +
                    "sous Linux, votre utilisateur doit appartenir au groupe « dialout »."
            );
        }

        try {
            pause(BOOT_DELAY_MS);   // l'ouverture du port réinitialise le pod
            PortInputStream in = new PortInputStream(port.getInputStream(), port);
            OutputStream out = port.getOutputStream();

            for (int attempt = 1; ; attempt++) {
                out.write(COMMAND);
                out.flush();
                try {
                    skipUntilMarker(in);
                    break;
                } catch (InterruptedIOException cancelled) {
                    throw cancelled;
                } catch (IOException e) {
                    if (attempt == MAX_ATTEMPTS) {
                        throw new IOException("Le pod ne répond pas. Vérifiez qu'il est branché, allumé "
                                + "et qu'il n'est pas en train d'enregistrer.", e);
                    }
                }
            }

            byte[] raw = in.readNBytes(4);

            if (raw.length < 4) throw new IOException("Réponse du pod incomplète.");

            long blocks = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
            if (blocks == 0) throw new IOException("La carte du pod est vide : rien à importer.");

            return new PodDump(in, blocks * BLOCK_SIZE);

        } catch (IOException | RuntimeException e) {
            port.closePort();
            throw e;
        }
    }

    // ===================
    // PRIVATE API
    // ===================

    private boolean looksLikeUsb(SerialPort p) {
        String text = (
                p.getSystemPortName() + " " +
                        p.getDescriptivePortName()
        ).toLowerCase(Locale.ROOT);

        return text.contains("usb") || text.contains("acm") || text.contains("ch340")
                || text.contains("ftdi") || text.contains("arduino");
    }

    private void skipUntilMarker(InputStream in) throws IOException {

        int matched = 0;
        while (matched < MARKER.length) {

            if (Thread.currentThread().isInterrupted())
                throw new InterruptedIOException("Import annulé.");

            int b = in.read();
            if (b < 0) throw new IOException("Aucune réponse du pod.");

            matched = (b == MARKER[matched]) ? matched + 1 : (b == MARKER[0] ? 1 : 0);
        }
    }

    private static void pause(long ms) throws InterruptedIOException {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException("Import annulé.");
        }
    }

    // Fermer le flux ferme le port. Une lecture à zéro octet signifie un délai dépassé.
    private static final class PortInputStream extends FilterInputStream {
        private final SerialPort port;

        PortInputStream(InputStream in, SerialPort port) {
            super(in);
            this.port = port;
        }

        @Override public int read(byte[] b, int off, int len) throws IOException {
            int n = super.read(b, off, len);
            if (n == 0 && len > 0)
                throw new IOException("Le pod n'envoie plus de données (délai de 5 s dépassé).");
            return n;
        }

        @Override public void close() { port.closePort(); }
    }
}
