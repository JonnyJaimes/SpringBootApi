package com.bezkoder.springjwt.services.interfaces;



import com.bezkoder.springjwt.dto.CohorteDTO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CohorteServiceInterface {

    public void abrirCohorte(CohorteDTO cohorte);

    public void cerrarCohorte();

    public CohorteDTO comprobarCohorte();

    public List<CohorteDTO> listarCohorte();

    public void habilitarEnlace(String enlace);

     public CohorteDTO habilitarPrueba(String enlace , LocalDateTime fechaMaxPrueba);

    public void ampliarFechaFinCohorte(Date nuevaFechaFin);

}
