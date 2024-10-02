package com.bezkoder.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
import java.util.List;

/**
 * Clase de entidad que representa la tabla "aspirante" en la base de datos.
 * @author Gibson Arbey, Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "aspirante")
public class AspiranteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cohorte_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CohorteEntity cohorte;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @Column(nullable = false, length = 50)
    @NotEmpty(message = "El apellido no puede estar vacío")
    private String apellido;

    @Column(nullable = false, length = 20)
    @NotEmpty(message = "El género no puede estar vacío")
    private String genero;

    @Column(nullable = false, length = 50)
    @NotEmpty(message = "El lugar de nacimiento no puede estar vacío")
    private String lugar_nac;

    @Column(nullable = false)
    @NotNull(message = "La fecha de expedición del documento no puede estar vacía")
    private LocalDate fecha_exp_di;

    @Column(nullable = false)
    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate fecha_nac;

    @Column(nullable = false, length = 20)
    @NotEmpty(message = "El número de documento no puede estar vacío")
    private String no_documento;

    @Column(name = "correo_personal", nullable = false, length = 255)
    @NotEmpty(message = "El correo personal no puede estar vacío")
    @Email(message = "El formato del correo personal no es válido")
    private String correoPersonal;

    @Column(nullable = false, length = 50)
    @NotEmpty(message = "El departamento de residencia no puede estar vacío")
    private String departamento_residencia;

    @Column(length = 50)
    private String municipio_residencia;

    @Column(length = 100)
    private String direccion_residencia;

    @Column(nullable = false, length = 30)
    @NotEmpty(message = "El teléfono no puede estar vacío")
    private String telefono;

    @Column(nullable = false, length = 100)
    @NotEmpty(message = "El nombre de la empresa no puede estar vacío")
    private String empresa_trabajo;

    @Column(nullable = false, length = 50)
    @NotEmpty(message = "El departamento de trabajo no puede estar vacío")
    private String departamento_trabajo;

    @Column(nullable = false, length = 50)
    @NotEmpty(message = "El municipio de trabajo no puede estar vacío")
    private String municipio_trabajo;

    @Column(nullable = false, length = 100)
    @NotEmpty(message = "La dirección de trabajo no puede estar vacía")
    private String direccion_trabajo;

    @Column(nullable = false)
    @NotEmpty(message = "Los estudios de pregrado no pueden estar vacíos")
    private String estudios_pregrado;

    @Column(nullable = false)
    @NotEmpty(message = "Los estudios de posgrado no pueden estar vacíos")
    private String estudios_posgrados;

    @Column(nullable = false)
    @NotEmpty(message = "La experiencia laboral no puede estar vacía")
    private String exp_laboral;

    @Column(nullable = false)
    @NotNull(message = "Debe indicar si es egresado de la UFPS")
    private Boolean es_egresado_ufps;

    @Transient
    private Double total;

    @NotNull(message = "El puntaje de notas no puede ser nulo")
    private Integer puntajeNotas;

    @NotNull(message = "El puntaje de distinciones académicas no puede ser nulo")
    private Double puntajeDistincionesAcademicas;

    @NotNull(message = "El puntaje de experiencia laboral no puede ser nulo")
    private Double puntajeExperienciaLaboral;

    @NotNull(message = "El puntaje de publicaciones no puede ser nulo")
    private Double puntajePublicaciones;

    @NotNull(message = "El puntaje de cartas de referencia no puede ser nulo")
    private Integer puntajeCartasReferencia;

    @Transient
    private Double puntajeDocumentos;

    @NotNull(message = "El puntaje de la entrevista no puede ser nulo")
    private Integer puntaje_entrevista;

    @NotNull(message = "El puntaje de la prueba no puede ser nulo")
    private Integer puntaje_prueba;

    private LocalDateTime fecha_entrevista;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoEntity estado;

    @JsonIgnore
    @OneToMany(mappedBy = "aspirante", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoEntity> documentos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "aspirante", orphanRemoval = true)
    private List<NotificacionEntity> notificaciones = new ArrayList<>();

    public Double getTotal() {
        if (getPuntajeDocumentos() == null) setPuntajeDocumentos(0d);
        if (getPuntaje_entrevista() == null) setPuntaje_entrevista(0);
        if (getPuntaje_prueba() == null) setPuntaje_prueba(0);

        return getPuntajeDocumentos() + getPuntaje_entrevista() + getPuntaje_prueba();
    }

    public Double getPuntajeDocumentos() {
        if (getPuntajeCartasReferencia() == null) setPuntajeCartasReferencia(0);
        if (getPuntajeNotas() == null) setPuntajeNotas(0);
        if (getPuntajeDistincionesAcademicas() == null) setPuntajeDistincionesAcademicas(0d);
        if (getPuntajeExperienciaLaboral() == null) setPuntajeExperienciaLaboral(0d);
        if (getPuntajePublicaciones() == null) setPuntajePublicaciones(0d);

        return getPuntajeDistincionesAcademicas() + getPuntajeExperienciaLaboral() + getPuntajePublicaciones() + getPuntajeCartasReferencia() + getPuntajeNotas();
    }



}
