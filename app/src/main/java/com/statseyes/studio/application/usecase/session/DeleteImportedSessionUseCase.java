package com.statseyes.studio.application.usecase.session;

import com.statseyes.studio.application.port.ImportedSessionRepositoryPort;
import org.springframework.stereotype.Component;


@Component
public class DeleteImportedSessionUseCase {

    private final ImportedSessionRepositoryPort repository;

    public DeleteImportedSessionUseCase(ImportedSessionRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(Integer importedSessionId) {
        repository.deleteById(importedSessionId);
    }
}
