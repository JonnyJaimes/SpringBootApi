package com.bezkoder.springjwt.services.implementations;


import com.bezkoder.springjwt.UTIL.Utileria;
import com.bezkoder.springjwt.dto.DocumentoDTO;
import com.bezkoder.springjwt.models.AspiranteEntity;
import com.bezkoder.springjwt.models.DocumentoEntity;
import com.bezkoder.springjwt.models.EstadoDocEntity;
import com.bezkoder.springjwt.models.TipoDocumentoEntity;
import com.bezkoder.springjwt.payload.response.AspiranteEstadoDocResponse;
import com.bezkoder.springjwt.payload.response.DocumentoResponse;
import com.bezkoder.springjwt.repository.AspiranteRepository;
import com.bezkoder.springjwt.repository.DocumentoRepository;
import com.bezkoder.springjwt.repository.EstadoDocRepository;
import com.bezkoder.springjwt.repository.TipoDocumentoRepository;
import com.bezkoder.springjwt.services.interfaces.DocumetoServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bezkoder.springjwt.UTIL.Utileria.obtenerExtensionArchivo;
import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
public class DocumentoService implements DocumetoServiceInterface {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private EstadoDocRepository estadoDocRepository;

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Autowired
    private AspiranteRepository aspiranteRepository;

    @Value("${local.storage.basepath}")
    private String storageBasePath;

    @Override
    public DocumentoDTO subirDocumento(Integer aspiranteId, String tipoDocumento, MultipartFile file) {
        // Buscar el tipo de documento
        TipoDocumentoEntity tipoDocEntity = tipoDocumentoRepository.findByNombre(tipoDocumento);

        // Guardar archivo en el sistema de archivos
        String archivoUrl = Utileria.guardarArchivo(file, storageBasePath);
        if (archivoUrl == null) {
            throw new IllegalArgumentException("El archivo tiene un formato no permitido.");
        }

        // Crear DocumentoEntity y guardarlo en la base de datos
        DocumentoEntity documento = new DocumentoEntity();
        documento.setAspirante(aspiranteRepository.findById(aspiranteId).orElseThrow(() -> new IllegalArgumentException("Aspirante no encontrado")));
        documento.setDocumento(tipoDocEntity);
        documento.setUrl(archivoUrl);
        documento.setFormato(obtenerExtensionArchivo(file.getOriginalFilename())); // Obtener formato del archivo
        documento.setFecha(LocalDate.now()); // Agregar la fecha de subida
        documento.setEstado(estadoDocRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"))); // Estado inicial

        documentoRepository.save(documento);

        return new DocumentoDTO(documento.getId(), aspiranteId, tipoDocEntity.getId(), documento.getEstado().getId(), archivoUrl, documento.getFormato(), documento.getRetroalimentacion(), documento.getFecha(), documento.getComentarios(), documento.getArchivo());
    }



    @Override
    public List<DocumentoResponse> listarDocumentos(Integer aspiranteId) {
        List<DocumentoEntity> documentos = documentoRepository.findByAspiranteId(aspiranteId);
        return documentos.stream()
                .map(doc -> new DocumentoResponse(doc.getEstado(), doc.getDocumento(),doc.getFormato(), doc.getUrl()))
                .collect(Collectors.toList());
    }

    @Override
    public void cambiarEstadoDocumento(Integer documentoId, String nuevoEstado) {
        DocumentoEntity documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));

        EstadoDocEntity estadoDoc = estadoDocRepository.findByNombre(nuevoEstado);

        documento.setEstado(estadoDoc);
        documentoRepository.save(documento);

        // Enviar notificación basada en el nuevo estado
        enviarNotificacion(documento.getAspirante().getId(), estadoDoc.getNombre()); // Get the aspirante's ID
    }


    private void enviarNotificacion(Integer aspiranteId, String estadoDocumento) {
        // Implementar lógica para enviar notificación
    }
    public void cambiarEstadoDocumento(Integer aspiranteId, Integer documentoId, Integer nuevoEstadoId) {
        DocumentoEntity documentoEntity = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));
        EstadoDocEntity estado = estadoDocRepository.findById(nuevoEstadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        documentoEntity.setEstado(estado);
        documentoRepository.save(documentoEntity);
    }

    // Method to list documents by aspirant's email
    public List<DocumentoEntity> listarDocumentosPorAspirante(String email) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findByEmail(email);
        if (!aspirante.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return documentoRepository.findByAspiranteId(aspirante.get().getId());
    }


    // Method to list documents for a specific aspirant by ID
    public List<DocumentoEntity> listarDocumentosDeAspirante(Integer aspiranteId) {
        aspiranteRepository.findById(aspiranteId)
                .orElseThrow(() -> new IllegalArgumentException("Aspirante no encontrado"));
        return documentoRepository.findByAspiranteId(aspiranteId);
    }


    // Method to send feedback for a document
    public void EnviarRetroalimentacion(Integer aspiranteId, Integer docId, String retroalimentacion) {
        DocumentoEntity documentoEntity = documentoRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));
        EstadoDocEntity estadoRechazado = estadoDocRepository.findById(3) // Assuming 3 is the ID for "Rejected" state
                .orElseThrow(() -> new IllegalArgumentException("Estado rechazado no encontrado"));

        documentoEntity.setEstado(estadoRechazado);
        documentoEntity.setRetroalimentacion(retroalimentacion);
        documentoRepository.save(documentoEntity);
    }


    // Method to list aspirants with a specific document state
    public List<AspiranteEstadoDocResponse> listarAspirantesConEstadoDoc(Integer idEstado) {
        List<DocumentoEntity> documentos = documentoRepository.findByEstadoId(idEstado);
        return documentos.stream()
                .map(doc -> {
                    AspiranteEstadoDocResponse response = new AspiranteEstadoDocResponse();
                    AspiranteEntity aspirante = doc.getAspirante();
                    response.setNombre(aspirante.getNombre());
                    response.setApellido(aspirante.getApellido());
                    response.setCorreoPersonal(aspirante.getCorreoPersonal());
                    response.setTelefono(aspirante.getTelefono());
                    response.setEstado(doc.getEstado());
                    return response;
                })
                .collect(Collectors.toList());
    }


    // Method to create documents for an aspirant
    public List<DocumentoEntity> crearDocumentos(Integer aspiranteId) {
        AspiranteEntity aspirante = aspiranteRepository.findById(aspiranteId)
                .orElseThrow(() -> new IllegalArgumentException("Aspirante no encontrado"));

        List<TipoDocumentoEntity> tiposDocumentos = tipoDocumentoRepository.findAll();
        List<DocumentoEntity> documentos = new ArrayList<>();

        for (TipoDocumentoEntity tipoDocumento : tiposDocumentos) {
            DocumentoEntity documento = new DocumentoEntity();
            documento.setAspirante(aspirante);
            documento.setDocumento(tipoDocumento);
            documento.setEstado(estadoDocRepository.findById(1) // Assuming 1 is the ID for "Pending" state
                    .orElseThrow(() -> new IllegalArgumentException("Estado inicial no encontrado")));
            documentoRepository.save(documento);
            documentos.add(documento);
        }

        return documentos;
    }

    // Method to list files of an aspirant
    public List<DocumentoResponse> listFiles(Integer aspiranteId) {
        File directory = new File(storageBasePath + "/" + aspiranteId);
        List<DocumentoResponse> documentoResponses = new ArrayList<>();

        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                DocumentoResponse response = new DocumentoResponse();
                response.setFormato(getFileExtension(file));
                response.setUrl(file.getAbsolutePath());
                documentoResponses.add(response);
            }
        }
        return documentoResponses;
    }

    // Method to download a file
    public File downloadFile(String fileName) {
        File file = new File(storageBasePath + "/" + fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        return file;
    }

    // Method to delete a file
    public boolean deleteFile(String fileName) {
        File file = new File(storageBasePath + "/" + fileName);
        return file.exists() && file.delete();
    }

    // Method to upload a file
    public String uploadFile(int tipoDocumento, MultipartFile file) throws IOException {
        File directory = new File(storageBasePath + "/" + tipoDocumento);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directories if they do not exist
        }

        File uploadedFile = new File(directory, file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(uploadedFile)) {
            fos.write(file.getBytes());
        }
        return uploadedFile.getAbsolutePath();
    }

    // Helper method to get file extension
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        if (lastIndex != -1) {
            return name.substring(lastIndex + 1);
        }
        return "";
    }
}


