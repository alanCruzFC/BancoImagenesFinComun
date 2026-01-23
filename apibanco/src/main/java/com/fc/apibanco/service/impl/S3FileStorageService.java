package com.fc.apibanco.service.impl;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fc.apibanco.service.FileStorageService;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    public S3FileStorageService() {
        // El cliente S3 se auto-configura si usas Spring Cloud AWS,
        // pero como usamos el SDK directo, confiamos en la cadena de proveedores por
        // defecto
        // (DefaultCredentialsProvider: env vars, system props, profile, instance
        // profile).
        this.s3Client = S3Client.builder().build();
    }

    @Override
    public void createFolder(String path) throws IOException {
        // En S3 los folders no existen realmente, son prefijos.
        // No es necesario crear nada explícitamente a menos que queramos un objeto
        // marcador de 0 bytes.
    }

    @Override
    public String store(MultipartFile file, String path, String filename) throws IOException {
        String key = buildKey(path, filename);

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putOb, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return key; // Retornamos la key o la URL s3://...
        } catch (S3Exception e) {
            throw new IOException("Error subiendo archivo a S3", e);
        }
    }

    @Override
    public Resource load(String path, String filename) throws IOException {
        String key = buildKey(path, filename);
        try {
            GetObjectRequest getOb = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // Retornamos un InputStreamResource del stream de S3
            return new InputStreamResource(Objects.requireNonNull(s3Client.getObject(getOb)));
        } catch (S3Exception e) {
            throw new IOException("Error descargando archivo de S3", e);
        }
    }

    @Override
    public void delete(String path, String filename) throws IOException {
        String key = buildKey(path, filename);
        try {
            DeleteObjectRequest delOb = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(delOb);
        } catch (S3Exception e) {
            throw new IOException("Error borrando archivo de S3", e);
        }
    }

    private String buildKey(String path, String filename) {
        // Eliminar slashes iniciales/finales para evitar //
        String cleanPath = path.replace("\\", "/");
        if (cleanPath.startsWith("/"))
            cleanPath = cleanPath.substring(1);
        if (cleanPath.endsWith("/"))
            cleanPath = cleanPath.substring(0, cleanPath.length() - 1);

        return cleanPath + "/" + filename;
    }
}
