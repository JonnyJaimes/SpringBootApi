package com.bezkoder.springjwt.controllers;


import com.bezkoder.springjwt.models.DocumentoEntity;
import com.bezkoder.springjwt.payload.request.RetroAlimentacionRequest;
import com.bezkoder.springjwt.payload.response.AspiranteEstadoDocResponse;
import com.bezkoder.springjwt.payload.response.DocumentoUserResponse;
import com.bezkoder.springjwt.payload.response.DocumentoResponse;

import com.bezkoder.springjwt.repository.TipoDocumentoRepository;
import com.bezkoder.springjwt.services.implementations.DocumentoService;
import com.bezkoder.springjwt.services.implementations.NotificacionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    private TipoDocumentoRepository documentoRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('ENCARGADO')")
    @PutMapping("/aprobar/{documentoId}/{aspiranteId}")
    public ResponseEntity<String> aprobarDocumento(@PathVariable Integer documentoId, @PathVariable Integer aspiranteId) {
        documentoService.cambiarEstadoDocumento(aspiranteId, documentoId, 4);
        return ResponseEntity.ok("Documento aprobado con éxito");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ENCARGADO')")
    @PutMapping("/rechazar/{documentoId}/{aspiranteId}")
    public ResponseEntity<String> rechazarDocumento(@PathVariable Integer documentoId, @PathVariable Integer aspiranteId) {
        documentoService.cambiarEstadoDocumento(aspiranteId, documentoId, 3);
        return ResponseEntity.ok("Documento rechazado con éxito");
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/listar")
    public List<DocumentoUserResponse> listarDocumentosPorAspirante() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();
        List<DocumentoEntity> documentos = documentoService.listarDocumentosPorAspirante(email);
        List<DocumentoUserResponse> documentoResponses = new ArrayList<>();

        if (documentos != null) {
            for (DocumentoEntity documento : documentos) {
                DocumentoUserResponse documentoResponse = new DocumentoUserResponse();
                documentoResponse.setNombre(documento.getDocumento().getNombre());
                documentoResponse.setEstado(documento.getEstado());
                documentoResponse.setUrl_formato(documento.getDocumento().getUrl_formato());
                documentoResponse.setIdDocumento(documento.getDocumento().getId());
                documentoResponses.add(documentoResponse);
            }
        }
        return documentoResponses;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ENCARGADO')")
    @GetMapping("/listarDoc")
    public List<DocumentoResponse> listarDocumentosdeAspirante(@RequestParam("aspiranteId") Integer aspiranteId) {
        List<DocumentoEntity> documentos = documentoService.listarDocumentosDeAspirante(aspiranteId);
        return documentos.stream()
                .map(this::mapDocumentoEntityAResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ENCARGADO')")
    @PostMapping("/retroalimentacion")
    public ResponseEntity<String> rechazaryEnviarRetroalimentacion(@RequestBody @Valid RetroAlimentacionRequest retroalimentacionRequest) {
        documentoService.EnviarRetroalimentacion(retroalimentacionRequest.getAspiranteId(),
                retroalimentacionRequest.getDocId(), retroalimentacionRequest.getRetroalimentacion());

        String documentName = documentoRepository.findById(retroalimentacionRequest.getDocId())
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"))
                .getNombre();

        notificacionService.crearNotificacion("El documento " + documentName + " ha sido rechazado. Razón: " + retroalimentacionRequest.getRetroalimentacion(),
                retroalimentacionRequest.getAspiranteId());

        return ResponseEntity.ok("Documento rechazado y retroalimentación enviada");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ENCARGADO')")
    @GetMapping("/filtrar")
    public List<AspiranteEstadoDocResponse> listarAspirantesConEstado(@RequestParam Integer idEstado) {
        return documentoService.listarAspirantesConEstadoDoc(idEstado);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/crearDocs")
    public ResponseEntity<String> crearDocumentos(@RequestParam Integer aspiranteId) {
        List<DocumentoEntity> documentos = documentoService.crearDocumentos(aspiranteId);
        String message = (!documentos.isEmpty()) ? "Documentos creados exitosamente" : "No se pudieron crear los documentos.";
        return ResponseEntity.ok(message);
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
