package com.ufps.maestria.services.interfaces;

import com.ufps.maestria.dto.DocumentoDTO;
import com.ufps.maestria.models.DocumentoEntity;
import com.ufps.maestria.payload.response.AspiranteEstadoDocResponse;
import com.ufps.maestria.payload.response.DocumentoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Esta interfaz define los métodos necesarios para guardar, buscar y recuperar documentos.
 */
public interface DocumetoServiceInterface {

    DocumentoDTO subirDocumento(Integer aspiranteId, String tipoDocumento, MultipartFile file);

    List<DocumentoResponse> listarDocumentos(Integer aspiranteId);

    int countFilesByAspiranteId(Integer aspiranteId);

    void cambiarEstadoDocumento(Integer aspiranteId, Integer documentoId, Integer estadoId);

    public List<DocumentoEntity> listarDocumentosPorAspirante(String email);

    public List<DocumentoEntity> listarDocumentosDeAspirante(Integer aspiranteId);

    public void EnviarRetroalimentacion(Integer aspiranteId, Integer docId, String retroalimentacion);

    public List<AspiranteEstadoDocResponse> listarAspirantesConEstadoDoc(Integer idEstado);

    public List<DocumentoEntity> crearDocumentos(Integer aspiranteId);

    public void cambiarEstadoDocumentoV2(Integer aspiranteId, Integer documentoId, Integer estadoId);




}