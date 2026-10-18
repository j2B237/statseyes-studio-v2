package com.statseyes.studio.domain.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.statseyes.studio.domain.config.ApplicationConfiguration;
import com.statseyes.studio.domain.config.Resource_Type;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    public Path getUploadDirectory(Resource_Type dir) {

        Path uploadDir = Paths.get(System.getProperty(
                ApplicationConfiguration.USER_HOME.getValue()),
                ApplicationConfiguration.UPLOAD_SUBDIR.getValue(),
                dir.getValue());
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de créer le dossier upload des logos", e);
        }
        return uploadDir;
    }

    public Optional<Path> findExistingImage(Integer Id, Resource_Type dir) {

        Path uploadDir = getUploadDirectory(dir);

        String prefix = (switch (dir){
            case Resource_Type.CLUB ->  "club-";
            case Resource_Type.ATHLETE -> "athlete-";
        }) + Id + "-";

        try (Stream<Path> files = Files.list(uploadDir)) {
            return files
                    .filter(p -> p.getFileName().toString().startsWith(prefix))
                    .findFirst();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lister le dossier upload des logos", e);
        }
    }

    public String saveClubLogo(Path sourceFile, Integer Id) throws IOException {
        if (sourceFile == null) {
            throw new IllegalArgumentException("sourceFile cannot be null");
        }

        String extension = getFileExtension(sourceFile);

        String filename = "club-" + Id + "-" + UUID.randomUUID() + extension;
        Path target = getUploadDirectory(Resource_Type.CLUB).resolve(filename);

        Files.copy(sourceFile, target, StandardCopyOption.REPLACE_EXISTING);

        // On ne stocke QUE le nom du fichier, jamais le chemin complet.
        return filename;
    }

    public String saveAthleteImage(Path sourceFile, Integer id) throws IOException {
        if(sourceFile == null){
            throw new IllegalArgumentException("sourceFile cannot be null");
        }

        String extension = getFileExtension(sourceFile);
        String filename = "athlete-" + id + "-" + UUID.randomUUID() + extension;
        Path target = getUploadDirectory(Resource_Type.ATHLETE).resolve(filename);

        Files.copy(sourceFile, target, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    public String getLogoUrl(String filename, Resource_Type dir) {
        if (filename == null || filename.isBlank()) {
            return null;
        }
        return getUploadDirectory(dir).resolve(filename).toUri().toString();
    }

    // =================
    // PRIVATE API
    // =================

    private String getFileExtension(Path sourceFile) throws IOException {
        String originalName = sourceFile.getFileName().toString();
        String extension = "";

        int dot = originalName.lastIndexOf('.');
        if (dot > 0) {
            extension = originalName.substring(dot);
        }
        return extension;
    }
}
