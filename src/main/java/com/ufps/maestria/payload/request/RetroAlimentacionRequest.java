package com.ufps.maestria.payload.request;

import lombok.Data;

@Data
public class RetroAlimentacionRequest {

    private String retroalimentacion;
    private Integer docId;
    private Integer aspiranteId;


}
