package com.ufps.maestria.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CohorteAspirantesResponse {

    private Integer aspiranteId;
    private String nombre;
    private String apellido;
    private String genero;
    private String lugarNacimiento;
    private LocalDate fechaNacimiento;
    private String noDocumento;
    private String correoPersonal;
    private String departamentoResidencia;
    private String municipioResidencia;
    private String direccionResidencia;
    private String telefono;
    private String estadoDescripcion; // This can be the description of the current state of the aspirant
}