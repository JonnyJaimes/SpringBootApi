package com.ufps.maestria.payload.response;

import com.ufps.maestria.models.EstadoDocEntity;
import lombok.Data;

@Data
public class DocumentoUserResponse {

    private String nombre;

    private EstadoDocEntity estado;

    private String url_formato;

    private int idDocumento;

}
