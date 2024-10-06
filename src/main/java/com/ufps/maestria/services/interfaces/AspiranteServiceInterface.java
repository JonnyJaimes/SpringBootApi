package com.ufps.maestria.services.interfaces;

import com.ufps.maestria.dto.AspiranteDTO;
import com.ufps.maestria.dto.UserDTO;
import com.ufps.maestria.payload.response.AspiranteCohorteResponse;

import java.time.LocalDateTime;
import java.util.List;
public interface AspiranteServiceInterface {

    AspiranteDTO crearAspirante(AspiranteDTO aspirante, String email);
    
    public List<AspiranteDTO> listarAspirantesCohorteActual();

    public UserDTO getUserByAspirante(Integer id);

    public void disableAspirante(String email);

    public void enableAspirante(String email);

    AspiranteCohorteResponse getAspiranteByUserEmail(String email);

    public AspiranteDTO getAspiranteByAspiranteId (Integer aspiranteId);
  
    public AspiranteDTO getAspiranteById(int id);

    public void habilitarFechaEntrevista(Integer id, LocalDateTime fecha_entrevista);

    public void calificarPruebaAspirante (int id, int calificacionPrueba);

    public void calificarEntrevistaAspirante (int id, int calificacionEntrevista);

    public void admitirAspirante(Integer aspiranteId);

    public List<AspiranteDTO> listarAdmitidos(Integer estadoId);

    public List<AspiranteDTO> obtenerAspirantesHistoricosCohorte(Integer cohorteId);

    public void rechazarAdmisionAspirante(Integer aspiranteId);
  
    public void calificarDocsIndivi(Integer aspiranteId, Integer puntajeCartas, Integer puntajeNotasPregrado, Double puntajePublicaciones, Double puntajeDistinciones, Double puntajeExperiencia);

    public boolean cambiarEsEgresado(Integer aspiranteId);

    public boolean existsByEmail(String email);

    AspiranteDTO getAspiranteByEmail(String email);

    List<AspiranteDTO> getAllAspirantes();

    AspiranteDTO editAspirante(Integer id, AspiranteDTO aspiranteDTO);

    void deleteAspirante(Integer id);

    List<AspiranteDTO> getAspirantesByCohorte(Integer cohorteId);


    boolean rechazarAdmision(Integer aspiranteId);
}
