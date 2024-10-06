package com.ufps.maestria.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class CohorteDTO {

    private Integer id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean habilitado;
    private String enlace_entrevista;
    private String enlace_prueba;
    private LocalDateTime fechaMaxPrueba;
}
