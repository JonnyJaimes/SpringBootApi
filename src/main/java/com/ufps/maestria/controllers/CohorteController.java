
package com.ufps.maestria.controllers;

import com.ufps.maestria.dto.CohorteDTO;
import com.ufps.maestria.services.implementations.CohorteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/cohorte")
public class CohorteController {

    @Autowired
    CohorteService cohorteService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createCohorte(@RequestBody CohorteDTO cohorteDTO) {
        try {
            CohorteDTO createdCohorte = cohorteService.createCohorte(cohorteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCohorte);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating cohort: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping("/all")
    public ResponseEntity<List<CohorteDTO>> getAllCohortes() {
        try {
            List<CohorteDTO> cohortes = cohorteService.getAllCohortes();
            return ResponseEntity.ok(cohortes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cerrar")
    public ResponseEntity<String> cerrarCohorte() {
        try {
            cohorteService.cerrarCohorte();
            return ResponseEntity.ok("Se ha cerrado la cohorte correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cerrar la cohorte: " + e.getMessage());
        }
    }

    /**
     * Endpoint que amplía la fecha de finalización de un cohorte existente.
     *
     * @param nuevaFechaFin La nueva fecha de finalización del cohorte.
     * @return Un mensaje que indica si se amplió la fecha del cohorte correctamente o si ocurrió un error.
     */
    @PutMapping("/fechaFin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> ampliarFechaFinCohorte(@RequestParam("nuevaFechaFin") String nuevaFechaFin) {
        try {
            LocalDate date = LocalDate.parse(nuevaFechaFin, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            cohorteService.ampliarFechaFinCohorte(date);
            return ResponseEntity.ok("Se ha ampliado la fecha del cohorte correctamente");
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de fecha incorrecto. Debe ser 'yyyy-MM-dd'.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al ampliar la fecha de la cohorte: " + e.getMessage());
        }
    }

    @GetMapping("/abierto")
    public ResponseEntity<?> comprobarCohorte() {
        try {
            CohorteDTO cohorteDTO = cohorteService.comprobarCohorte();
            return ResponseEntity.ok(cohorteDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al comprobar la cohorte: " + e.getMessage());
        }
    }

    /**
     * Endpoint guardar Enlace
     * Este método guarda el enlace de la entrevista y retorna toda la información de la cohorte
     * junto con el enlace de la entrevista.
     *
     * @param enlace contiene el enlace de la entrevista.
     * @return un mensaje que indica que el enlace ha sido guardado.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/entrevistaEnlace")
    public ResponseEntity<String> guardarEnlace(@RequestParam String enlace) {
        try {
            cohorteService.habilitarEnlace(enlace);
            return ResponseEntity.ok("El enlace ha sido guardado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el enlace: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/prueba")
    public ResponseEntity<String> habilitarPrueba(@RequestParam String enlace, @RequestParam String fecha_prueba) {
        try {
            LocalDateTime fechaPrueba = LocalDateTime.parse(fecha_prueba, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            cohorteService.habilitarPrueba(enlace, fechaPrueba);
            return ResponseEntity.ok("Se ha habilitado la prueba correctamente");
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("La fecha de la prueba no tiene el formato correcto. Use 'yyyy-MM-ddTHH:mm:ss'");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al habilitar la prueba: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarCohorte(@PathVariable Integer id) {
        try {
            cohorteService.eliminarCohorte(id);
            return ResponseEntity.ok("Cohorte eliminada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la cohorte: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<String> editarCohorte(@PathVariable Integer id, @RequestBody CohorteDTO cohorteDTO) {
        try {
            cohorteService.editarCohorte(id, cohorteDTO);
            return ResponseEntity.ok("Cohorte actualizada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la cohorte: " + e.getMessage());
        }
    }

    @GetMapping("obtenerPorId/{id}")
    public ResponseEntity<CohorteDTO> getCohorteById(@PathVariable Integer id) {
        CohorteDTO cohorteDTO = cohorteService.getCohorteById(id);
        return ResponseEntity.ok(cohorteDTO);
    }

}



