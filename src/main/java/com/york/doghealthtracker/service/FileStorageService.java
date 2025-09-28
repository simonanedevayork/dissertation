package com.york.doghealthtracker.service;

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

@Service
public class FileStorageService {

    @Value("${app.file-storage.location}")
    private String storageLocation;

    public String store(String dogId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
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

    public Resource loadAsResource(String dogId, String filename) {
        try {
            Path file = Paths.get(storageLocation).resolve(dogId).resolve(filename);

            if (!Files.exists(file)) {
                throw new RuntimeException("File not found: " + filename);
            }
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file", e);
        }
    }

}