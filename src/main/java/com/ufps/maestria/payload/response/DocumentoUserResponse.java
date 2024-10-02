package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.EstadoDocEntity;
import lombok.Data;

@Data
public class DocumentoUserResponse {

    private String nombre;

    private EstadoDocEntity estado;

    private String url_formato;

    private int idDocumento;

}
