package com.statseyes.studio.application.usecase.session;

@FunctionalInterface
public interface ProgressListener {
    void onProgress(long done, long total);
}
