package com.bezkoder.springjwt.services.interfaces;

import com.bezkoder.springjwt.dto.DocumentoDTO;
import com.bezkoder.springjwt.payload.response.DocumentoResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Esta interfaz define los métodos necesarios para guardar, buscar y recuperar documentos.
 */
public interface DocumetoServiceInterface {

    DocumentoDTO subirDocumento(Integer aspiranteId, String tipoDocumento, MultipartFile file);
    List<DocumentoResponse> listarDocumentos(Integer aspiranteId);
    void cambiarEstadoDocumento(Integer documentoId, String nuevoEstado);



}