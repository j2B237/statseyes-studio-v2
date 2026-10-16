package com.statseyes.studio.presentation.concurrent;

import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;

@Component
public class BackgroundTaskRunner {

    public <T> void run(Callable<T> work, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.call();
            }
        };

        task.setOnSucceeded(e ->
                onSuccess.accept(task.getValue())
        );

        task.setOnFailed(e ->
                onFailure.accept(task.getException())
        );

        executor.submit(task);
    }

    public <T> Task<T> runWithProgress(
            ProgressWork<T> work,
            DoubleConsumer onProgress,
           Consumer<T> onSuccess, Consumer<Throwable> onFailure
    ) {

        Task<T> task = new Task<>() {
            @Override protected T call() throws Exception {
                return work.call(this::updateProgress);
            }
        };

        task.progressProperty().addListener(
                (o, ov, nv) ->
                        onProgress.accept(nv.doubleValue())
        );

        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onFailure.accept(task.getException()));
        task.setOnCancelled(e ->
                onFailure.accept(new CancellationException("Import annulé."))
        );

        executor.submit(task);
        return task;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    @FunctionalInterface
    public interface ProgressWork<T> {
        T call(BiConsumer<Long, Long> progress) throws Exception;
    }

    // ==============
    // PRIVATE API
    // ==============

    private final ExecutorService executor =
            Executors.newFixedThreadPool(4, runnable -> {
                Thread thread = new Thread(runnable, "pod-io-worker");
                thread.setDaemon(true);
                return thread;
    });


}