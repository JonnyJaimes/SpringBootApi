package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.EstadoDocEntity;
import lombok.Data;

@Data
public class AspiranteEstadoDocResponse {
    private String nombre;

    private String apellido;

    private String correoPersonal;

    private String telefono;

    private EstadoDocEntity estado;
}
