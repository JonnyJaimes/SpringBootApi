
package com.bezkoder.springjwt.payload.request;


import com.bezkoder.springjwt.dto.DocumentoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DocumentoRequest {
    /**
     * Mapa de documentos con clave como tipo de documento y valor como el archivo correspondiente.
     */
    private Map<String, DocumentoDTO> documentos;
}
