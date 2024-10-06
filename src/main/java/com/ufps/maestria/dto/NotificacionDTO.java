package com.ufps.maestria.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificacionDTO {
    private Integer id;
    private Integer aspiranteId;  // Referencia a AspiranteEntity
    private String enunciado;
    private Boolean estado;
    private Date fecha_envio;
}
