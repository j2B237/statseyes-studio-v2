package com.statseyes.studio.presentation.concurrent;

import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Component
public class BackgroundTaskRunner {

    private final ExecutorService executor = Executors.newFixedThreadPool(4, runnable -> {
        Thread thread = new Thread(runnable, "pod-io-worker");
        thread.setDaemon(true);
        return thread;
    });

    public <T> void run(Callable<T> work, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.call();
            }
        };
        // setOnSucceeded/setOnFailed s'executent garantis sur le thread JavaFX --
        // pas besoin de Platform.runLater manuel.
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onFailure.accept(task.getException()));
        executor.submit(task);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }
}