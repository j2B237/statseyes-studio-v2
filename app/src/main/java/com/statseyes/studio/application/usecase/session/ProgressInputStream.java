package com.statseyes.studio.application.usecase.session;


import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public final class ProgressInputStream  extends FilterInputStream {

    private final long total;
    private final ProgressListener listener;
    private long done;

    public ProgressInputStream(
            InputStream in,
            long total,
            ProgressListener listener
    ) {
        super(in);
        this.total = total;
        this.listener = listener;
    }

    @Override
    public int read() throws IOException {
        checkNotCancelled();
        int b = super.read();
        if (b >= 0) advance(1);
        return b;
    }

    @Override public int read(byte[] b, int off, int len) throws IOException {
        checkNotCancelled();
        int n = super.read(b, off, len);
        if (n > 0) advance(n);
        return n;
    }

    private void checkNotCancelled() throws InterruptedIOException {
        if (Thread.currentThread().isInterrupted())
            throw new InterruptedIOException("Import annulé.");
    }

    private void advance(int n) {
        done += n;
        listener.onProgress(done, total);
    }
}
