package com.york.doghealthtracker.service;

import com.york.doghealthtracker.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

/**\
 * Service responsible for storing files in, and loading files from, the local file system.
 * File storage location is injected from app.file-storage.location property in application.yml.
 */
@Service
public class FileStorageService {

    @Value("${app.file-storage.location}")
    private String storageLocation;

    /**
     * Stores a provided file into a local directory.
     * @param dogId The id of the dog corresponding to the saved file. Used to build the file path.
     * @param file The name of the saved file to be retrieved. Used to build the file path.
     * @return a String representing the stored file name.
     * @throws IOException if file is null or empty.
     */
    public String store(String dogId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int i = original.lastIndexOf('.');
        if (i >= 0) {
            ext = original.substring(i);
        }

        String generated = UUID.randomUUID().toString() + ext;

        Path dogFolder = Paths.get(storageLocation).resolve(dogId);
        Files.createDirectories(dogFolder);

        Path target = dogFolder.resolve(generated);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return generated;
    }

    /**
     * Retrieves and returns a stored file from the storage location as a Resource.
     * @param dogId The id of the dog corresponding to the saved file. Used to build the file path.
     * @param filename The name of the saved file to be retrieved. Used to build the file path.
     * @return Resource entity of the stored file.
     * @throws FileStorageException if file cannot be loaded properly.
     */
    public Resource load(String dogId, String filename) {
        try {
            Path file = Paths.get(storageLocation).resolve(dogId).resolve(filename);

            if (!Files.exists(file)) {
                throw new FileStorageException("File not found: " + filename);
            }
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new FileStorageException("Could not read file", e);
        }
    }

}