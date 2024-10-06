package com.ufps.maestria.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class NotificacionResponse {


    private String enunciado;


    private Boolean estado;


    private Date fecha_envio;


}
