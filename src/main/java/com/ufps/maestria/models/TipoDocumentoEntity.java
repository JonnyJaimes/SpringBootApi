package com.ufps.maestria.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Clase de entidad que representa la tabla "tipo_documento" en la base de datos.
 * @author Gibson Arbey, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tipo_documento")
public class TipoDocumentoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único de la entidad.
     * Se genera automáticamente mediante el uso de la estrategia de generación de identificación IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nombre del tipo de documento
     */
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    /**
     * url del formato institucional para llenar de forma correcta la información (opcional)
     */
    @Column(nullable = true, length = 255)
    private String url_formato;



}