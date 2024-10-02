package com.bezkoder.springjwt.dto;

import lombok.Data;

@Data
public class TipoDocumentoDTO {
    private Integer id;
    private String nombre;
    private String url_formato;
}
