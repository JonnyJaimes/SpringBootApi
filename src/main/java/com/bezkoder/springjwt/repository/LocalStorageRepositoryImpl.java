package com.bezkoder.springjwt.repository;


import com.bezkoder.springjwt.models.DocumentoEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LocalStorageRepositoryImpl implements FileStorageRepository {

    @Value("${local.storage.basepath}")
    private String basePath;

    @Override
    public List<DocumentoEntity> listFiles(String directoryPath) {
        List<DocumentoEntity> items = new ArrayList<>();
        Path dirPath = Paths.get(basePath, directoryPath);

        try {
            Files.walk(dirPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        DocumentoEntity dto = new DocumentoEntity();
                        try {
                            dto.setUrl(path.toUri().toURL().toString());  // Conversión a String
                        } catch (MalformedURLException e) {
                            e.printStackTrace();  // Manejo adecuado de la excepción
                        }

                        items.add(dto);
                    });
        } catch (IOException e) {
            e.printStackTrace();  // Manejo adecuado de la excepción
        }
        return items;
    }


    @Override
    public InputStream getFileInputStream(String filePath) throws IOException {
        Path fullPath = Paths.get(basePath, filePath);
        if (Files.exists(fullPath)) {
            return Files.newInputStream(fullPath);  // Uso de Files.newInputStream para simplificar
        } else {
            throw new FileNotFoundException("Archivo no encontrado: " + fullPath.toString());
        }
    }

    @Override
    public byte[] downloadFile(String filePath) throws IOException {
        Path fullPath = Paths.get(basePath, filePath);
        if (Files.exists(fullPath)) {
            return Files.readAllBytes(fullPath);  // Leer el contenido del archivo como bytes
        } else {
            throw new FileNotFoundException("Archivo no encontrado: " + fullPath.toString());
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        Path fullPath = Paths.get(basePath, filePath);
        try {
            return Files.deleteIfExists(fullPath);  // Elimina el archivo si existe
        } catch (IOException e) {
            e.printStackTrace();  // Manejo adecuado de errores
            return false;
        }
    }

    @Override
    public boolean uploadFile(String filePath, File fileObj) throws IOException {
        Path targetPath = Paths.get(basePath, filePath);
        Files.createDirectories(targetPath.getParent());  // Crear directorios si no existen
        try {
            Files.copy(fileObj.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            if (fileObj.exists()) {
                fileObj.delete();  // Eliminar el archivo temporal solo si la copia fue exitosa
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();  // Manejo adecuado de errores
            return false;
        }
    }

    @Override
    public boolean createDirectory(String directoryPath) {
        Path dirPath = Paths.get(basePath, directoryPath);
        try {
            Files.createDirectories(dirPath);  // Crear directorios si no existen
            return true;
        } catch (IOException e) {
            e.printStackTrace();  // Manejo adecuado de errores
            return false;
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        Path fullPath = Paths.get(basePath, filePath);
        if (Files.exists(fullPath)) {
            return fullPath.toUri().toString();  // Devuelve la URL del archivo
        } else {
            return null;  // Manejo de caso en el que no existe el archivo
        }
    }
}
