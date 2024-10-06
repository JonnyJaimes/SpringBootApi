package com.ufps.maestria.controllers;


import com.ufps.maestria.models.DocumentoEntity;
import com.ufps.maestria.payload.request.RetroAlimentacionRequest;
import com.ufps.maestria.payload.response.AspiranteEstadoDocResponse;
import com.ufps.maestria.payload.response.DocumentoUserResponse;
import com.ufps.maestria.payload.response.DocumentoResponse;

import com.ufps.maestria.repository.TipoDocumentoRepository;
import com.ufps.maestria.services.implementations.DocumentoService;
import com.ufps.maestria.services.implementations.NotificacionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/documentosEstados")
public class DocumentoEstadosController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PutMapping("/aprobar/{documentoId}/{aspiranteId}")
    public ResponseEntity<String> aprobarDocumento(@PathVariable Integer documentoId, @PathVariable Integer aspiranteId) {
        try {
            // Get the ID for the "approved" state dynamically
            Integer approvedStateId = tipoDocumentoRepository.findByNombre("Aprobado")
                    .orElseThrow(() -> new EntityNotFoundException("Estado 'Aprobado' no encontrado.")).getId();

            documentoService.cambiarEstadoDocumento(aspiranteId, documentoId, approvedStateId);
            return ResponseEntity.ok("Documento aprobado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PutMapping("/rechazar/{documentoId}/{aspiranteId}")
    public ResponseEntity<String> rechazarDocumento(@PathVariable Integer documentoId, @PathVariable Integer aspiranteId) {
        try {
            // Get the ID for the "rejected" state dynamically
            Integer rejectedStateId = tipoDocumentoRepository.findByNombre("Rechazado")
                    .orElseThrow(() -> new EntityNotFoundException("Estado 'Rechazado' no encontrado.")).getId();

            documentoService.cambiarEstadoDocumento(aspiranteId, documentoId, rejectedStateId);
            return ResponseEntity.ok("Documento rechazado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("Aspirante/listar")
    public ResponseEntity<List<DocumentoUserResponse>> listarDocumentosPorAspirante() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // Get the authenticated user's email

            List<DocumentoEntity> documentos = documentoService.listarDocumentosPorAspirante(email);
            List<DocumentoUserResponse> documentoResponses = documentos.stream()
                    .map(doc -> {
                        DocumentoUserResponse response = new DocumentoUserResponse();
                        response.setNombre(doc.getDocumento().getNombre());
                        response.setEstado(doc.getEstado());
                        response.setUrl_formato(doc.getDocumento().getUrl_formato());
                        response.setIdDocumento(doc.getDocumento().getId());
                        return response;
                    }).collect(Collectors.toList());

            return ResponseEntity.ok(documentoResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping("Aspirantes/listarDoc")
    public ResponseEntity<List<DocumentoResponse>> listarDocumentosdeAspirante(@RequestParam("aspiranteId") Integer aspiranteId) {
        try {
            List<DocumentoEntity> documentos = documentoService.listarDocumentosDeAspirante(aspiranteId);
            List<DocumentoResponse> response = documentos.stream()
                    .map(this::mapDocumentoEntityAResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PostMapping("/retroalimentacion")
    public ResponseEntity<String> rechazaryEnviarRetroalimentacion(@RequestBody @Valid RetroAlimentacionRequest retroalimentacionRequest) {
        try {
            documentoService.EnviarRetroalimentacion(retroalimentacionRequest.getAspiranteId(),
                    retroalimentacionRequest.getDocId(), retroalimentacionRequest.getRetroalimentacion());

            String documentName = tipoDocumentoRepository.findById(retroalimentacionRequest.getDocId())
                    .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"))
                    .getNombre();

            notificacionService.crearNotificacion("El documento " + documentName + " ha sido rechazado. Razón: " + retroalimentacionRequest.getRetroalimentacion(),
                    retroalimentacionRequest.getAspiranteId());

            return ResponseEntity.ok("Documento rechazado y retroalimentación enviada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping("/filtrar")
    public ResponseEntity<List<AspiranteEstadoDocResponse>> listarAspirantesConEstado(@RequestParam Integer idEstado) {
        try {
            List<AspiranteEstadoDocResponse> response = documentoService.listarAspirantesConEstadoDoc(idEstado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/crearDocs")
    public ResponseEntity<String> crearDocumentos(@RequestParam Integer aspiranteId) {
        try {
            List<DocumentoEntity> documentos = documentoService.crearDocumentos(aspiranteId);
            String message = (!documentos.isEmpty()) ? "Documentos creados exitosamente" : "No se pudieron crear los documentos.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private DocumentoResponse mapDocumentoEntityAResponse(DocumentoEntity documento) {
        DocumentoResponse documentoResponse = new DocumentoResponse();
        documentoResponse.setEstado(documento.getEstado());
        documentoResponse.setUrl(documento.getUrl());
        documentoResponse.setFormato(documento.getFormato());
        documentoResponse.setDocumento(documento.getDocumento());
        return documentoResponse;
    }
}
