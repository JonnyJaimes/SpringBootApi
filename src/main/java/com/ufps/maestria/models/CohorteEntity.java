package com.ufps.maestria.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cohorte", uniqueConstraints= {@UniqueConstraint(columnNames= {"id"})})
public class CohorteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único de la entidad.
     * Se genera automáticamente mediante el uso de la estrategia de generación de identificación IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    /**
     * Fecha de inicio de la cohorte
     */
    @Column(name = "fecha_inicio",nullable = false)
    @NotNull(message = "La fecha de inicio no puede estar vacía")
    private LocalDate fechaInicio;

    /**
     * Fecha de finalización de la cohorte
     */
    @Column(name = "fecha_fin",nullable = false)
    @NotNull(message = "La fecha de inicio no puede estar vacía")
    private LocalDate fechaFin;

    /**
     * La cohorte esta habilitada
     */
    @Column(nullable = false)
    @NotNull
    private Boolean habilitado;

    /**
     * Enlace de ingreso a la entrevista
     */
    @Column(length = 255)
    @NotEmpty
    private String enlace_entrevista;


    /**
     * Enlace para presentar la prueba
     */
    @Column(length = 255)
    @NotEmpty
    private String enlace_prueba;

    /**
     * Fecha hasta la que va a estar habilitada la prueba
     */
    @Column(name = "fecha_max_prueba")
    @NotNull
    private LocalDateTime fechaMaxPrueba;

    /**
     * Relación uno a muchos entre Cohorte y Aspirante
     */
    @JsonIgnore
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "cohorte")
    private List<AspiranteEntity> aspirantes = new ArrayList<>();

}