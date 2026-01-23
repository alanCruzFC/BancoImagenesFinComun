package com.fc.apibanco.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fc.apibanco.service.FileStorageService;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.archivos.ruta-base}")
    private String basePathProp;

    private Path getBasePath() {
        return Paths.get(basePathProp).normalize();
    }

    @Override
    public void createFolder(String path) throws IOException {
        Path folder = getBasePath().resolve(path).normalize();
        if (!folder.startsWith(getBasePath())) {
            throw new IOException("Ruta fuera del directorio base permitido");
        }
        Files.createDirectories(folder);
    }

    @Override
    public String store(MultipartFile file, String path, String filename) throws IOException {
        Path folder = getBasePath().resolve(path).normalize();
        createFolder(path); // Asegurar que existe

        Path destination = folder.resolve(filename).normalize();
        if (!destination.startsWith(getBasePath())) {
            throw new IOException("Ruta fuera del directorio base permitido");
        }

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }

    @Override
    public Resource load(String path, String filename) throws IOException {
        Path file = getBasePath().resolve(path).resolve(filename).normalize();
        try {
            Resource resource = new UrlResource(Objects.requireNonNull(file.toUri(), "URI cannot be null"));
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("No se puede leer el archivo: " + filename);
            }
        } catch (Exception e) {
            throw new IOException("Error al cargar archivo: " + filename, e);
        }
    }

    @Override
    public void delete(String path, String filename) throws IOException {
        Path file = getBasePath().resolve(path).resolve(filename).normalize();
        Files.deleteIfExists(file);
    }
}
