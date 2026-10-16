package com.statseyes.studio.domain.model;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public record PodDump(
        InputStream stream,
        long totalBytes
) implements Closeable {
    @Override public void close() throws IOException { stream.close(); }
}