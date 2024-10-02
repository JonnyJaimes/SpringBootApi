package com.bezkoder.springjwt.UTIL;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Utileria {

    // Only allowed file types
    private static final List<String> EXTENSIONES_PERMITIDAS = Arrays.asList("pdf", "docx", "jpg", "jpeg", "png");

    /**
     * Saves the file on the disk only if it has a valid file type.
     *
     * @param multiPart MultipartFile received from the form.
     * @param ruta      Path where the file will be saved.
     * @return The final name of the file or null if it could not be saved.
     */
    public static String guardarArchivo(MultipartFile multiPart, String ruta) {
        // Get the original file name.
        String nombreOriginal = multiPart.getOriginalFilename();
        // Replace spaces with dashes in the file name.
        nombreOriginal = nombreOriginal.replace(" ", "-");

        // Get the file extension
        String extension = obtenerExtensionArchivo(nombreOriginal);

        // Verify that the file has a permitted extension
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            System.out.println("Error: Tipo de archivo no permitido. Solo se permiten: " + EXTENSIONES_PERMITIDAS);
            return null;
        }

        // Add 8 random characters to the file name to avoid duplicates.
        String nombreFinal = randomAlphaNumeric(8) + "-" + nombreOriginal;
        try {
            // Form the file name to save it to disk.
            File archivo = new File(ruta + nombreFinal);
            System.out.println("Archivo: " + archivo.getAbsolutePath());
            // Save the file physically on disk.
            multiPart.transferTo(archivo);
            return nombreFinal;
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Auxiliary method to get the file extension.
     *
     * @param nombreArchivo The name of the file.
     * @return The extension of the file (e.g., "pdf", "jpg").
     */
    public static String obtenerExtensionArchivo(String nombreArchivo) {
        if (nombreArchivo.lastIndexOf(".") != -1 && nombreArchivo.lastIndexOf(".") != 0) {
            return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
        } else {
            return "";  // No extension
        }
    }

    /**
     * Method to generate a random alphanumeric string of length N
     *
     * @param count Length of the string.
     * @return Random alphanumeric string of length N.
     */
    public static String randomAlphaNumeric(int count) {
        String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * CARACTERES.length());
            builder.append(CARACTERES.charAt(character));
        }
        return builder.toString();
    }
}
