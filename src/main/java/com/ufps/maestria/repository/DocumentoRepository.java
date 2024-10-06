package com.ufps.maestria.repository;


import com.ufps.maestria.models.AspiranteEntity;
import com.ufps.maestria.models.DocumentoEntity;
import com.ufps.maestria.models.EstadoDocEntity;
import com.ufps.maestria.models.TipoDocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository que define los métodos para acceder a los documentos del
 * Aspirante.
 * @author Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Gibson Arbey, Juan Pablo Correa Tarazona, Miguel Angel Lara
 */
@Repository
public interface DocumentoRepository extends JpaRepository<DocumentoEntity, Integer> {

    /**
     * Buscar documentos por aspirante.
     *
     * @param aspirante El aspirante al que se le van a buscar los documentos.
     * @return Un listado con los documentos del aspirante.
     */
    List<DocumentoEntity> findByAspirante(AspiranteEntity aspirante);

    /**
     * Buscar documentos por ID de tipo de documento.
     *
     * @param tipoDocumentoId El ID del tipo de documento a buscar.
     * @return Un listado con los documentos que cumplen con el tipo a buscar.
     */
    List<DocumentoEntity> findByDocumento_Id(Integer tipoDocumentoId);

    /**
     * Buscar documentos por tipo.
     *
     * @param documento El tipo de documento a buscar.
     * @return Un listado con los documentos que cumplen con el tipo a buscar.
     */
    List<DocumentoEntity> findByDocumento(TipoDocumentoEntity documento);

    /**
     * Buscar documentos por aspirante y estado.
     *
     * @param aspirante El aspirante al que se le van a buscar los documentos.
     * @param estado El estado de los documentos a buscar.
     * @return Un listado con los documentos que cumplen con el aspirante y el estado.
     */
    List<DocumentoEntity> findByAspiranteAndEstado(AspiranteEntity aspirante, EstadoDocEntity estado);

    /**
     * Buscar un objeto DocumentoEntity por aspirante y tipo de documento.
     *
     * @param idAspirante El ID del aspirante para buscar el documento.
     * @param idTipoDocumento El ID del tipo de documento para buscar el documento.
     * @return Un objeto DocumentoEntity que corresponde al aspirante y tipo de documento proporcionados.
     */
    @Query(value = "SELECT * FROM documento D WHERE D.documento_id = :idtipo_documento AND D.aspirante_id = :idAspirante", nativeQuery = true)
    DocumentoEntity findByAspiranteAndDocumento(@Param("idAspirante") Integer idAspirante, @Param("idtipo_documento") Integer idTipoDocumento);

    /**
     * Buscar documentos por estado y aspirante.
     *
     * @param idEstado El ID del estado del documento.
     * @param idAspirante El ID del aspirante para buscar los documentos.
     * @return Una lista de objetos DocumentoEntity que corresponden al estado y aspirante proporcionados.
     */
    @Query(value = "SELECT * FROM documento D WHERE D.estado_id = :idEstado AND D.aspirante_id = :idAspirante", nativeQuery = true)
    List<DocumentoEntity> findDocumentosByEstado(@Param("idEstado") Integer idEstado, @Param("idAspirante") Integer idAspirante);

    /**
     * Contar la cantidad de documentos por aspirante.
     *
     * @param aspirante El aspirante para el cual contar los documentos.
     * @return La cantidad de documentos asociados al aspirante.
     */
    Integer countByAspirante(AspiranteEntity aspirante);


    /**
     * Buscar documentos por ID de aspirante.
     *
     * @param aspiranteId El ID del aspirante.
     * @return Una lista con los documentos del aspirante.
     */
    List<DocumentoEntity> findByAspirante_Id(Integer aspiranteId);

    /**
     * Buscar documentos por ID de estado.
     *
     * @param estadoId El ID del estado.
     * @return Una lista con los documentos que tienen el estado especificado.
     */
    List<DocumentoEntity> findByEstado_Id(Integer estadoId);

    /**
     * Contar la cantidad de documentos por ID de aspirante.
     *
     * @param aspiranteId El ID del aspirante.
     * @return El número de documentos asociados al aspirante.
     */
    int countByAspirante_Id(Integer aspiranteId);
}

