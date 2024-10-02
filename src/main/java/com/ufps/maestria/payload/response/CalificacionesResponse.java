package com.bezkoder.springjwt.payload.response;


import lombok.Data;

@Data
public class CalificacionesResponse {
    private Integer puntajeNotas;
    private Double puntajeDistincionesAcademicas;
    private Double puntajeExperienciaLaboral;
    private Double puntajePublicaciones;
    private Integer puntajeCartasReferencia;
    private Integer puntajeEntrevista;
    private Integer puntajePrueba;
    private Double total;
}
