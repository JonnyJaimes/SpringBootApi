package com.bezkoder.springjwt.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstadoDoc {
    private Integer id;

    /**
     * Nombre del estado
     */
    @Column(nullable = false, length = 20)
    @NotEmpty
    private String nombre;

}
