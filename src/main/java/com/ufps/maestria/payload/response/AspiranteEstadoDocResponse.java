package com.ufps.maestria.payload.response;

import com.ufps.maestria.models.EstadoDocEntity;
import lombok.Data;

@Data
public class AspiranteEstadoDocResponse {
    private String nombre;

    private String apellido;

    private String correoPersonal;

    private String telefono;

    private EstadoDocEntity estado;
}
