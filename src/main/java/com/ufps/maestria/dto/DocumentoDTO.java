package com.ufps.maestria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoDTO {
    private Integer id;
    private Integer aspiranteId;  // Referencia a AspiranteEntity
    private Integer documentoId;  // Referencia a TipoDocumentoEntity
    private Integer estadoId;     // Referencia a EstadoDocEntity
    private String url;
    private String formato;
    private String retroalimentacion;
    private LocalDate fecha;
    private String comentarios;
    private String archivo;
}
