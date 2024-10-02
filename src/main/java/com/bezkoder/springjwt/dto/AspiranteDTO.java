package com.bezkoder.springjwt.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AspiranteDTO {

    private Integer id;


    private Integer cohorteId;

    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotEmpty(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotEmpty(message = "El género no puede estar vacío")
    private String genero;

    @NotEmpty(message = "El lugar de nacimiento no puede estar vacío")
    private String lugar_nac;

    @NotNull(message = "La fecha de expedición no puede estar vacía")
    private LocalDate fecha_exp_di;

    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate fecha_nac;

    @NotEmpty(message = "El número de documento no puede estar vacío")
    private String no_documento;

    @NotEmpty(message = "El correo personal no puede estar vacío")
    private String correoPersonal;

    @NotEmpty(message = "El departamento de residencia no puede estar vacío")
    private String departamento_residencia;

    private String municipio_residencia;
    private String direccion_residencia;

    @NotEmpty(message = "El teléfono no puede estar vacío")
    private String telefono;

    @NotEmpty(message = "La empresa de trabajo no puede estar vacía")
    private String empresa_trabajo;

    @NotEmpty(message = "El departamento de trabajo no puede estar vacío")
    private String departamento_trabajo;

    private String municipio_trabajo;
    private String direccion_trabajo;

    @NotEmpty(message = "Los estudios de pregrado no pueden estar vacíos")
    private String estudios_pregrado;

    @NotEmpty(message = "Los estudios de posgrado no pueden estar vacíos")
    private String estudios_posgrados;

    @NotEmpty(message = "La experiencia laboral no puede estar vacía")
    private String exp_laboral;

    @NotNull(message = "Es obligatorio indicar si es egresado de la UFPS")
    private Boolean es_egresado_ufps;

    private Double total;
    private Integer puntajeNotas;
    private Double puntajeDistincionesAcademicas;
    private Double puntajeExperienciaLaboral;
    private Double puntajePublicaciones;
    private Integer puntajeCartasReferencia;
    private Integer puntaje_entrevista;
    private Integer puntaje_prueba;

    private LocalDateTime fecha_entrevista;

    @NotNull(message = "El estado es obligatorio")
    private Integer estadoId; // Referencia a la entidad Estado
}

