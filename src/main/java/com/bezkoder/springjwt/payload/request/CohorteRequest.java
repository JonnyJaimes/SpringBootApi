package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class CohorteRequest {
    
    private Date fechaInicio;

    private Date fechaFin;
}
