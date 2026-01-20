package com.fc.apibanco.service;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Crea un folder o base path si es necesario.
     * En S3 esto es conceptual (prefijos), en disco es real.
     */
    void createFolder(String path) throws IOException;

    /**
     * Guarda un archivo en la ruta especificada.
     * 
     * @param file     Archivo a guardar
     * @param path     Ruta relativa (folder)
     * @param filename Nombre del archivo destino
     * @return Ruta completa o URL del archivo guardado
     */
    String store(MultipartFile file, String path, String filename) throws IOException;

    /**
     * Carga un recurso.
     * 
     * @param path     Ruta relativa
     * @param filename Nombre del archivo
     * @return Recurso cargable (UrlResource o S3Resource)
     */
    Resource load(String path, String filename) throws IOException;

    /**
     * Elimina un archivo.
     */
    void delete(String path, String filename) throws IOException;

    /**
     * Copia un InputStream a un destino (útil para migraciones o casos especiales)
     * si es necesario, o mantenemos simple la interfaz.
     */
}
