package com.statseyes.studio.domain.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class LogoService {

    private static final String UPLOAD_SUBDIR = ".statseyes/uploads/clubs";

    public Path getUploadDirectory() {
        Path uploadDir = Paths.get(System.getProperty("user.home"), UPLOAD_SUBDIR);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de créer le dossier d’upload des logos", e);
        }
        return uploadDir;
    }

    public Optional<Path> findExistingLogo(Integer clubId) {
        Path uploadDir = getUploadDirectory();
        String prefix = "club-" + clubId + "-";

        try (Stream<Path> files = Files.list(uploadDir)) {
            return files
                    .filter(p -> p.getFileName().toString().startsWith(prefix))
                    .findFirst();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lister le dossier d'upload des logos", e);
        }
    }

    public String saveLogo(Path sourceFile, Integer clubId) throws IOException {
        if (sourceFile == null) {
            throw new IllegalArgumentException("sourceFile cannot be null");
        }

        String originalName = sourceFile.getFileName().toString();
        String extension = "";

        int dot = originalName.lastIndexOf('.');
        if (dot > 0) {
            extension = originalName.substring(dot);
        }

        String filename = "club-" + clubId + "-" + UUID.randomUUID() + extension;
        Path target = getUploadDirectory().resolve(filename);

        Files.copy(sourceFile, target, StandardCopyOption.REPLACE_EXISTING);

        // On ne stocke QUE le nom du fichier, jamais le chemin complet.
        return filename;
    }

    public String getLogoUrl(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }
        return getUploadDirectory().resolve(filename).toUri().toString();
    }
}
