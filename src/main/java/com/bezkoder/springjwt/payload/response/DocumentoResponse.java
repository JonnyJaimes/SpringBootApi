package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.EstadoDocEntity;
import com.bezkoder.springjwt.models.TipoDocumentoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoResponse {
    /**
     * Estado del documento(rechazado, aprobado, en espera..)
     */
    private EstadoDocEntity estado;
    /**
     * tipo de documento(foto, cv, notas de pregrado...)
     */
    private TipoDocumentoEntity documento;
    /**
     *
     */
    private String formato;
    private String url;
}