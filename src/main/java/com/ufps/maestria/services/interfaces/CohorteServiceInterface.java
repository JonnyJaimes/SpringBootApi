package com.ufps.maestria.services.interfaces;



import com.ufps.maestria.dto.CohorteDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CohorteServiceInterface {
/*
    public void abrirCohorte(CohorteDTO cohorte);

    public void cerrarCohorte();

    public CohorteDTO comprobarCohorte();

    public List<CohorteDTO> listarCohorte();

    public void habilitarEnlace(String enlace);

     public CohorteDTO habilitarPrueba(String enlace , LocalDateTime fechaMaxPrueba);

    public void ampliarFechaFinCohorte(Date nuevaFechaFin);
    */

    public void ampliarFechaFinCohorte(LocalDate nuevaFechaFin);
    public void cerrarCohorte();
    public CohorteDTO comprobarCohorte();
    public void habilitarEnlace(String enlace);
    public void habilitarPrueba(String enlace, LocalDateTime fechaPrueba);
    public void eliminarCohorte(Integer id);
    public void editarCohorte(Integer id, CohorteDTO cohorteDTO);
    public CohorteDTO createCohorte(CohorteDTO cohorteDTO);
    public List<CohorteDTO> getAllCohortes();

}
