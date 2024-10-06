package com.ufps.maestria.services.implementations;


import com.ufps.maestria.dto.CohorteDTO;
import com.ufps.maestria.models.CohorteEntity;
import com.ufps.maestria.repository.CohorteRepository;
import com.ufps.maestria.repository.FileStorageRepository;
import com.ufps.maestria.services.interfaces.CohorteServiceInterface;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;


/**
 * @author Juan Pablo Correa Tarazona, Angel Yesid Duque Cruz, Gibson Arbey, Julian Camilo Riveros Fonseca, Ingrid Florez
 */
@Service
public class CohorteService implements CohorteServiceInterface {
    @Autowired
    private CohorteRepository cohorteRepository;

    @Autowired
    private FileStorageRepository fileStorageRepository;

    @Autowired
    private NotificacionService notificacionService;

    private static final String FILE_PATH = "localDateTime.txt";

    // Create a cohort
    public CohorteDTO createCohorte(CohorteDTO cohorteDTO) {
        CohorteEntity cohorteEntity = new CohorteEntity();
        BeanUtils.copyProperties(cohorteDTO, cohorteEntity);

        // Save the new cohort to the database
        CohorteEntity savedCohorte = cohorteRepository.save(cohorteEntity);

        // Convert saved entity back to DTO to return
        CohorteDTO createdCohorteDTO = new CohorteDTO();
        BeanUtils.copyProperties(savedCohorte, createdCohorteDTO);

        return createdCohorteDTO;
    }

    // Get all cohorts
    public List<CohorteDTO> getAllCohortes() {
        List<CohorteEntity> cohortes = cohorteRepository.findAll();
        // Convert List of CohorteEntity to List of CohorteDTO
        return cohortes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public CohorteDTO getCohorteById(Integer id) {
        CohorteEntity cohorteEntity = cohorteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohorte not found with id: " + id));

        // Convert entity to DTO
        return convertToDTO(cohorteEntity);
    }

    // Convert Entity to DTO
    private CohorteDTO convertToDTO(CohorteEntity cohorteEntity) {
        CohorteDTO cohorteDTO = new CohorteDTO();
        BeanUtils.copyProperties(cohorteEntity, cohorteDTO);
        return cohorteDTO;
    }

    // Close the current cohort
    public void cerrarCohorte() {
        CohorteEntity currentCohorte = cohorteRepository.findCohorteByHabilitado(true);
        currentCohorte.setHabilitado(false);
        cohorteRepository.save(currentCohorte);
    }

    // Extend the cohort's end date
    public void ampliarFechaFinCohorte(LocalDate nuevaFechaFin) {
        CohorteEntity currentCohorte = cohorteRepository.findCohorteByHabilitado(true);

        if (!nuevaFechaFin.isAfter(currentCohorte.getFechaFin())) {
            throw new IllegalArgumentException("La nueva fecha de finalización debe ser posterior a la fecha actual.");
        }

        currentCohorte.setFechaFin(nuevaFechaFin);
        cohorteRepository.save(currentCohorte);
    }

    // Check if a cohort is open
    public CohorteDTO comprobarCohorte() {
        CohorteEntity currentCohorte = cohorteRepository.findCohorteByHabilitado(true);

        return convertToDTO(currentCohorte);
    }

    // Save the interview link
    public void habilitarEnlace(String enlace) {
        CohorteEntity currentCohorte = cohorteRepository.findCohorteByHabilitado(true);

        currentCohorte.setEnlace_entrevista(enlace);
        cohorteRepository.save(currentCohorte);
    }

    // Enable the test with a link and date
    public void habilitarPrueba(String enlace, LocalDateTime fechaPrueba) {
        CohorteEntity currentCohorte = cohorteRepository.findCohorteByHabilitado(true);


        currentCohorte.setEnlace_prueba(enlace);
        currentCohorte.setFechaMaxPrueba(fechaPrueba);
        cohorteRepository.save(currentCohorte);
    }

    public void eliminarCohorte(Integer id) {
        CohorteEntity cohorte = cohorteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohorte no encontrada con el ID: " + id));
        cohorteRepository.delete(cohorte);
    }

    public void editarCohorte(Integer id, CohorteDTO cohorteDTO) {
        CohorteEntity cohorte = cohorteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohorte no encontrada con el ID: " + id));

        // Update cohorte details
        cohorte.setFechaInicio(cohorteDTO.getFechaInicio());
        cohorte.setFechaFin(cohorteDTO.getFechaFin());
        cohorte.setHabilitado(cohorteDTO.getHabilitado());
        cohorte.setEnlace_entrevista(cohorteDTO.getEnlace_entrevista());
        cohorte.setEnlace_prueba(cohorteDTO.getEnlace_prueba());
        cohorte.setFechaMaxPrueba(cohorteDTO.getFechaMaxPrueba());

        // Save the updated cohorte
        cohorteRepository.save(cohorte);
    }
}

