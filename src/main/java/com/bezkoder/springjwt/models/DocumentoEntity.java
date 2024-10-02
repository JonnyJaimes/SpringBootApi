package com.bezkoder.springjwt.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * Clase de entidad que representa la tabla "documento" en la base de datos.
 * @author Gibson Arbey, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documento")
public class DocumentoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "aspirante_id", nullable = false, updatable = false)
    private AspiranteEntity aspirante;

    @ManyToOne
    @JoinColumn(name = "documento_id", nullable = false, updatable = false)
    private TipoDocumentoEntity documento;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoDocEntity estado;

    @Column(nullable = true)
    private String url;

    @Column(nullable = false)
    private String formato;

    @Column(nullable = true)
    private String retroalimentacion;

    @Column(nullable = false)
    @NotNull
    private LocalDate fecha;

    @Column(nullable = true)
    private String comentarios;

    @Column(nullable = true)
    private String archivo;

}