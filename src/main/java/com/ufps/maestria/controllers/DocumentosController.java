package com.bezkoder.springjwt.controllers;


import com.bezkoder.springjwt.dto.DocumentoDTO;
import com.bezkoder.springjwt.payload.response.DocumentoResponse;

import com.bezkoder.springjwt.services.implementations.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/documentos")
public class DocumentosController {

    @Autowired
    private DocumentoService documentoService;  // Assuming a service for handling documents

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/uploadFile/{aspiranteId}/{tipoDocumento}")
    public ResponseEntity<?> uploadFile(@PathVariable Integer aspiranteId,
                                        @PathVariable String tipoDocumento,
                                        @RequestParam("file") MultipartFile file) {
        try {
            DocumentoDTO documentoDTO = documentoService.subirDocumento(aspiranteId, tipoDocumento, file);
            return ResponseEntity.ok(documentoDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



    @GetMapping("/listFiles/{idAspirante}")
    public ResponseEntity<List<DocumentoResponse>> listarDocumentosAspirante(@PathVariable Integer idAspirante) {
        List<DocumentoResponse> documentoResponses = documentoService.listFiles(idAspirante);
        return new ResponseEntity<>(documentoResponses, HttpStatus.OK);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam(value = "fileName") String fileName) throws IOException {
        File file = documentoService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(java.nio.file.Files.readAllBytes(file.toPath()));

        return ResponseEntity.ok()
                .contentLength(file.length())
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/deleteObject")
    public ResponseEntity<String> deleteFile(@RequestParam(value = "fileName") String fileName) {
        if (documentoService.deleteFile(fileName)) {
            return ResponseEntity.ok("File deleted");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while deleting the document");
        }
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/uploadFile/{tipoDocumento}")
    public ResponseEntity<String> uploadFile(@PathVariable("tipoDocumento") int tipoDocumento,
                                             @RequestParam("file") MultipartFile file) {
        try {
            // Check if file is empty
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
            }

            // Log the file details
            System.out.println("Uploading file: " + file.getOriginalFilename());

            // Validate tipoDocumento
            if (tipoDocumento <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid document type.");
            }

            // Upload the file
            String filePath = documentoService.uploadFile(tipoDocumento, file);

            return ResponseEntity.ok("File uploaded successfully to path: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while uploading the file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

}
