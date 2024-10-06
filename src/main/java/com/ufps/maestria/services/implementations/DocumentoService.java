package com.ufps.maestria.services.implementations;

import com.ufps.maestria.UTIL.Utileria;
import com.ufps.maestria.dto.DocumentoDTO;
import com.ufps.maestria.models.AspiranteEntity;
import com.ufps.maestria.models.DocumentoEntity;
import com.ufps.maestria.models.EstadoDocEntity;
import com.ufps.maestria.models.TipoDocumentoEntity;
import com.ufps.maestria.payload.response.AspiranteEstadoDocResponse;
import com.ufps.maestria.payload.response.DocumentoResponse;
import com.ufps.maestria.repository.AspiranteRepository;
import com.ufps.maestria.repository.DocumentoRepository;
import com.ufps.maestria.repository.EstadoDocRepository;
import com.ufps.maestria.repository.TipoDocumentoRepository;
import com.ufps.maestria.services.interfaces.DocumetoServiceInterface;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.stream.Collectors;

import static com.ufps.maestria.UTIL.Utileria.obtenerExtensionArchivo;


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
        TipoDocumentoEntity tipoDocEntity = tipoDocumentoRepository.findByNombre(tipoDocumento)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado."));

        // Guardar archivo en el sistema de archivos
        String archivoUrl = Utileria.guardarArchivo(file, storageBasePath);
        if (archivoUrl == null) {
            throw new IllegalArgumentException("El archivo tiene un formato no permitido.");
        }

        // Crear DocumentoEntity y guardarlo en la base de datos
        AspiranteEntity aspirante = aspiranteRepository.findById(aspiranteId)
                .orElseThrow(() -> new IllegalArgumentException("Aspirante no encontrado"));

        DocumentoEntity documento = new DocumentoEntity();
        documento.setAspirante(aspirante);
        documento.setDocumento(tipoDocEntity);
        documento.setUrl(archivoUrl);
        documento.setFormato(obtenerExtensionArchivo(file.getOriginalFilename()));
        documento.setFecha(LocalDate.now());
        documento.setEstado(estadoDocRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado")));

        documentoRepository.save(documento);

        return new DocumentoDTO(documento.getId(), aspiranteId, tipoDocEntity.getId(), documento.getEstado().getId(),
                archivoUrl, documento.getFormato(), documento.getRetroalimentacion(),
                documento.getFecha(), documento.getComentarios(), documento.getArchivo());
    }


    @Override
    public List<DocumentoResponse> listarDocumentos(Integer aspiranteId) {
        // Check if aspirante exists
        aspiranteRepository.findById(aspiranteId)
                .orElseThrow(() -> new IllegalArgumentException("Aspirante no encontrado"));

        List<DocumentoEntity> documentos = documentoRepository.findByAspirante_Id(aspiranteId);
        return documentos.stream()
                .map(doc -> new DocumentoResponse(doc.getEstado(), doc.getDocumento(), doc.getFormato(), doc.getUrl()))
                .collect(Collectors.toList());
    }


    @Override
    public int countFilesByAspiranteId(Integer aspiranteId) {
        return documentoRepository.countByAspirante_Id(aspiranteId);
    }

    private void enviarNotificacion(Integer aspiranteId, String estadoDocumento) {
        // Implementar lógica para enviar notificación
    }


    @Override
    public void cambiarEstadoDocumento(Integer aspiranteId, Integer documentoId, Integer nuevoEstadoId) {
        DocumentoEntity documentoEntity = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));

        if (!documentoEntity.getAspirante().getId().equals(aspiranteId)) {
            throw new IllegalArgumentException("El documento no pertenece al aspirante especificado.");
        }

        EstadoDocEntity estado = estadoDocRepository.findById(nuevoEstadoId)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        documentoEntity.setEstado(estado);
        documentoRepository.save(documentoEntity);
    }


    // Method to list documents by aspirant's email

    @Override
    public List<DocumentoEntity> listarDocumentosPorAspirante(String email) {
        AspiranteEntity aspirante = aspiranteRepository.findByCorreoPersonal(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return documentoRepository.findByAspirante_Id(aspirante.getId());
    }

    @Override
    public List<DocumentoEntity> listarDocumentosDeAspirante(Integer aspiranteId) {
        AspiranteEntity aspirante = aspiranteRepository.findById(aspiranteId)
                .orElseThrow(() -> new IllegalArgumentException("Aspirante no encontrado"));

        return documentoRepository.findByAspirante_Id(aspirante.getId());
    }

    // Method to send feedback for a document

    @Override
    public void EnviarRetroalimentacion(Integer aspiranteId, Integer docId, String retroalimentacion) {
        DocumentoEntity documentoEntity = documentoRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));

        if (!documentoEntity.getAspirante().getId().equals(aspiranteId)) {
            throw new IllegalArgumentException("El documento no pertenece al aspirante especificado.");
        }

        EstadoDocEntity estadoRechazado = estadoDocRepository.findByNombre("Rejected");

        documentoEntity.setEstado(estadoRechazado);
        documentoEntity.setRetroalimentacion(retroalimentacion);
        documentoRepository.save(documentoEntity);
    }



    // Method to list aspirants with a specific document state
    @Override
    public List<AspiranteEstadoDocResponse> listarAspirantesConEstadoDoc(Integer idEstado) {
        List<DocumentoEntity> documentos = documentoRepository.findByEstado_Id(idEstado);
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
    @Override
    public List<DocumentoEntity> crearDocumentos(Integer aspiranteId) {
        AspiranteEntity aspirante = aspiranteRepository.findById(aspiranteId)
                .orElseThrow(() -> new IllegalArgumentException("Aspirante no encontrado"));

        EstadoDocEntity estadoInicial = estadoDocRepository.findByNombre("Pending");

        List<TipoDocumentoEntity> tiposDocumentos = tipoDocumentoRepository.findAll();
        List<DocumentoEntity> documentos = new ArrayList<>();

        for (TipoDocumentoEntity tipoDocumento : tiposDocumentos) {
            DocumentoEntity documento = new DocumentoEntity();
            documento.setAspirante(aspirante);
            documento.setDocumento(tipoDocumento);
            documento.setEstado(estadoInicial);
            documentoRepository.save(documento);
            documentos.add(documento);
        }

        return documentos;
    }


    @Override
    public void cambiarEstadoDocumentoV2(Integer aspiranteId, Integer documentoId, Integer estadoId) {
        DocumentoEntity documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado"));

        if (!documento.getAspiranteId().equals(aspiranteId)) {
            throw new IllegalArgumentException("El documento no pertenece al aspirante.");
        }

        EstadoDocEntity nuevoEstado = estadoDocRepository.findById(estadoId)
                .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado"));

        // Set the new state
        documento.setEstado(nuevoEstado);
        documentoRepository.save(documento);
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


