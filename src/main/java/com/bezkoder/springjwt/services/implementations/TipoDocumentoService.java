package com.bezkoder.springjwt.services.implementations;


import com.bezkoder.springjwt.dto.TipoDocumentoDTO;
import com.bezkoder.springjwt.models.TipoDocumentoEntity;
import com.bezkoder.springjwt.repository.TipoDocumentoRepository;
import com.bezkoder.springjwt.services.interfaces.TipoDocumentoServiceInterface;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar los tipos de documentos.
 * Define qué tipo de documentos son aceptados y sus características.
 * Por ejemplo: documentos en PDF, JPG, etc.
 * @author Julian Camilo Riveros FOnseca
 */
@Service
public class TipoDocumentoService implements TipoDocumentoServiceInterface {

    @Autowired
    TipoDocumentoRepository tipoDocumentoRepository;

    /**
     Devuelve una lista con todos los tipos de documentos permitidos para subida.
     @return lista de tipo TipoDocumentoDTO
     */
    @Override
    public List<TipoDocumentoDTO> listar() {
        List<TipoDocumentoEntity> tipoDocumentoEntities = tipoDocumentoRepository.findAll();
        return tipoDocumentoEntities.stream()
                .map(this::mapTipoDocumento)
                .collect(Collectors.toList());
    }

    /**
     Convierte un TipoDocumentoDTO a TipoDocumentoEntity.
     @param tipoDocDTO objeto TipoDocumentoDTO
     @return objeto TipoDocumentoEntity
     */
    private TipoDocumentoEntity mapTipoDocumentoDTO(TipoDocumentoDTO tipoDocDTO) {
        TipoDocumentoEntity tipoDoc = new TipoDocumentoEntity();
        BeanUtils.copyProperties(tipoDocDTO, tipoDoc);
        return tipoDoc;
    }

    /**
     Convierte un TipoDocumentoEntity a TipoDocumentoDTO.
     @param tipoDocumentoEntity objeto TipoDocumentoEntity
     @return objeto TipoDocumentoDTO
     */
    private TipoDocumentoDTO mapTipoDocumento(TipoDocumentoEntity tipoDocumentoEntity) {
        TipoDocumentoDTO tipoDocumentoDTO = new TipoDocumentoDTO();
        BeanUtils.copyProperties(tipoDocumentoEntity, tipoDocumentoDTO);
        return tipoDocumentoDTO;
    }
}
