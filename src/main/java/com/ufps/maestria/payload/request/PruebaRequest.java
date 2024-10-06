package com.ufps.maestria.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PruebaRequest {
    
    private String enlace_prueba;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd'T'HH:mm:ss")
    private LocalDateTime fechaMaxPrueba;
}
