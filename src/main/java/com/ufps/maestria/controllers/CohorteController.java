
package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.dto.CohorteDTO;
import com.bezkoder.springjwt.payload.request.CohorteRequest;
import com.bezkoder.springjwt.services.implementations.CohorteService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/cohorte")
public class CohorteController {

    @Autowired
    CohorteService cohorteService;

    /**
     * Endpoint Abrir Cohorte
     * Abre una cohorte nueva en el sistema.
     *
     * @param cohorteRequest que contiene los datos de la cohorte a abrir.
     * @return Un mensaje que indica si se abrió correctamente la cohorte.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/abrir")
    public ResponseEntity<String> abrirCohorte(@RequestBody @Valid CohorteRequest cohorteRequest) {
        CohorteDTO cohorteDTO = new CohorteDTO();
        BeanUtils.copyProperties(cohorteRequest, cohorteDTO);

        cohorteService.abrirCohorte(cohorteDTO);

        return ResponseEntity.ok("Se ha abierto la cohorte correctamente");
    }

    /**
     * Endpoint Cerrar Cohorte
     * Cierra la cohorte actual en el sistema.
     *
     * @return Un mensaje que indica si se cerró correctamente la cohorte.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cerrar")
    public ResponseEntity<String> cerrarCohorte() {
        cohorteService.cerrarCohorte();
        return ResponseEntity.ok("Se ha cerrado la cohorte correctamente");
    }

    /**
     * Endpoint que amplía la fecha de finalización de un cohorte existente.
     *
     * @param nuevaFechaFin La nueva fecha de finalización del cohorte.
     * @return Un mensaje que indica si se amplió la fecha del cohorte correctamente o si ocurrió un error.
     * @throws ParseException
     * @throws IllegalArgumentException Si la nueva fecha de finalización no es posterior a la fecha de finalización actual
     *                                  del cohorte.
     * @throws RuntimeException         Si no hay un cohorte actualmente habilitado.
     */
    @PutMapping("/fechaFin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> ampliarFechaFinCohorte(@RequestParam("nuevaFechaFin") String nuevaFechaFin) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(nuevaFechaFin);
        cohorteService.ampliarFechaFinCohorte(date);
        return ResponseEntity.ok("Se ha ampliado la fecha del cohorte correctamente");
    }

    /**
     * Endpoint Comprobar Cohorte
     * Comprueba si hay una cohorte abierta en el sistema y devuelve la información de la cohorte actual.
     *
     * @return un dto de la cohorte
     */
    @GetMapping("/abierto")
    public CohorteDTO comprobarCohorte() {
        return cohorteService.comprobarCohorte();
    }

    /**
     * Endpoint Listar Cohortes
     * Retorna una lista de objetos que representan todas las cohortes en el sistema (Historico de cohortes).
     * Requiere rol de administrador para acceder a este endpoint.
     *
     * @return una lista con los cohorte DTOs
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<CohorteDTO> listarCohorte() {
        return cohorteService.listarCohorte();
    }

    /**
     * Endpoint guardar Enlace
     * Este metodo guarda el enlace de la entrevista y retorna toda la información de la cohorte
     * junto con el enlace de la entrevista
     * Requiere rol de administrador para acceder a este endpoint.
     *
     * @param enlace contiene el enlace de la entrevista.
     * @return un mensaje que indica que el enlace ha sido guardado.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/entrevistaEnlace")
    public ResponseEntity<String> guardarEnlace(@RequestParam String enlace) {
        cohorteService.habilitarEnlace(enlace);
        return ResponseEntity.ok("El enlace ha sido guardado exitosamente.");
    }

    /**
     * Habilita la prueba para la cohorte abierta como administrador.
     * Requiere rol de administrador para acceder a este endpoint.
     *
     * @param enlace El enlace de la prueba a habilitar.
     * @param fecha_prueba La fecha de la prueba.
     * @return Un mensaje que indica que la prueba ha sido habilitada correctamente.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/prueba")
    public ResponseEntity<String> habilitarPrueba(@RequestParam String enlace, @RequestParam String fecha_prueba) {
        LocalDateTime fechaPrueba = null;
        try {
            fechaPrueba = LocalDateTime.parse(fecha_prueba, DateTimeFormatter.ISO_ZONED_DATE_TIME); // Ejemplo formato fecha: 2021-05-20T00:00:00.000Z
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("La fecha de la prueba no tiene el formato correcto");
        }
        cohorteService.habilitarPrueba(enlace, fechaPrueba);
        return ResponseEntity.ok("Se ha habilitado la prueba correctamente");
    }
}
