package com.statseyes.studio.presentation.athletes.importing;

import com.statseyes.studio.application.usecase.session.ImportSessionFromDeviceUseCase;
import com.statseyes.studio.application.usecase.session.ListPodDevicesUseCase;
import com.statseyes.studio.domain.exception.PodFileFormatException;
import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.PodDevice;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;

@Component
public class PodImportViewModel {

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private final ObservableList<PodDevice> devices = FXCollections.observableArrayList();
    private final BooleanProperty showAllPorts = new SimpleBooleanProperty(false);
    private final BooleanProperty importing = new SimpleBooleanProperty(false);
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty errorMessage = new SimpleStringProperty();

    private final ListPodDevicesUseCase listDevices;
    private final ImportSessionFromDeviceUseCase importFromDevice;
    private final SessionAdapter session;
    private final BackgroundTaskRunner runner;
    private Task<?> currentTask;

    // Constructor
    public PodImportViewModel(
        ListPodDevicesUseCase listDevices,
        ImportSessionFromDeviceUseCase importFromDevice,
        SessionAdapter session,
        BackgroundTaskRunner runner
    ) {
        this.listDevices = listDevices;
        this.importFromDevice = importFromDevice;
        this.session = session;
        this.runner = runner;
    }

    // ======================
    // PUBLIC API
    // ======================

    public void reset() {
        cancel();
        devices.clear();
        showAllPorts.set(false);
        importing.set(false);
        progress.set(0);
        status.set(null);
        errorMessage.set(null);
    }

    public void refreshDevices() {
        boolean includeAll = showAllPorts.get();

        errorMessage.set(null);
        status.set("Recherche des périphériques…");
        runner.run(
                () -> listDevices.execute(includeAll),
                list -> {
                    devices.setAll(list);
                    status.set(list.isEmpty()
                            ? "Aucun pod détecté. Branchez-le, attendez quelques secondes puis cliquez sur Actualiser."
                            : "Sélectionnez le pod à importer.");
                },
                error -> {
                    status.set(null);
                    errorMessage.set("Détection impossible : " + describe(error));
                }
        );
    }

    public void startImport(
            PodDevice device,
            Integer athleteId,
            Consumer<ImportedSession> onImported
    ) {

        if (device == null || importing.get()) return;

        AuthenticatedUser user = session.getCurrentUser();   // lu sur le thread FX

        if (user == null) {
            errorMessage.set("Aucun utilisateur connecté.");
            return;
        }

        Integer accountId = user.id();

        errorMessage.set(null);
        importing.set(true);
        progress.set(ProgressIndicator.INDETERMINATE_PROGRESS);
        status.set("Connexion au pod… (quelques secondes)");

        currentTask = runner.runWithProgress(
                report ->
                        importFromDevice.execute(
                                device.id(), accountId, athleteId, report::accept),
                p -> {
                    progress.set(p);
                    status.set(String.format("Réception des données : %.0f %%", p * 100));
                },
                imported -> {
                    importing.set(false);
                    onImported.accept(imported); },
                error -> {
                    importing.set(false);
                    progress.set(0);
                    if (error instanceof CancellationException) {
                        status.set("Import annulé.");
                    } else {
                        status.set(null);
                        errorMessage.set(describe(error));
                    }
                }
        );
    }

    public void cancel() {
        if (currentTask != null)
            currentTask.cancel(true);
    }

    public ObservableList<PodDevice> devices() { return devices; }
    public BooleanProperty showAllPortsProperty() { return showAllPorts; }
    public BooleanProperty importingProperty() { return importing; }
    public DoubleProperty progressProperty() { return progress; }
    public StringProperty statusProperty() { return status; }
    public StringProperty errorMessageProperty() { return errorMessage; }


    // ===============
    // PRIVATE API
    // ===============

    // Le parseur enveloppe les IOException : on remonte le message utile (« le pod ne répond pas »…)
    private static String describe(Throwable e) {
        // On ne déballe que l'enveloppe du parseur ; sinon on garde le message déjà lisible
        if (e instanceof PodFileFormatException && e.getCause() instanceof IOException io && io.getMessage() != null) {
            return io.getMessage();
        }
        return e.getMessage();
    }
}
