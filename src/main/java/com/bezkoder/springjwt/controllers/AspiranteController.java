package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.dto.AspiranteDTO;
import com.bezkoder.springjwt.dto.UserDTO;

import com.bezkoder.springjwt.payload.request.AspiranteEntrevistaRequest;
import com.bezkoder.springjwt.payload.response.CalificacionesResponse;

import com.bezkoder.springjwt.security.services.UserDetailsImpl;

import com.bezkoder.springjwt.services.interfaces.AspiranteServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/aspirante")
public class AspiranteController {

    private final AspiranteServiceInterface aspiranteService;

    @Autowired
    public AspiranteController(AspiranteServiceInterface aspiranteService) {
        this.aspiranteService = aspiranteService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearAspirante(@RequestBody AspiranteDTO aspiranteDTO) {
        try {
            String email = obtenerEmailDelToken();
            AspiranteDTO aspiranteCreado = aspiranteService.crearAspirante(aspiranteDTO, email);
            return ResponseEntity.ok(aspiranteCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear aspirante: " + e.getMessage());
        }
    }

    @GetMapping("/miPerfil")
    public ResponseEntity<AspiranteDTO> obtenerMiAspirante(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            String email = userDetails.getEmail();
            AspiranteDTO aspirante = aspiranteService.getAspiranteByEmail(email);
            return new ResponseEntity<>(aspirante, HttpStatus.OK);
        } catch (UsernameNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get all aspirants, accessible only by ADMIN or MODERATOR roles
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping("/all")
    public ResponseEntity<List<AspiranteDTO>> getAllAspirantes() {
        List<AspiranteDTO> aspirantes = aspiranteService.getAllAspirantes();
        return ResponseEntity.ok(aspirantes);
    }

    private String obtenerEmailDelToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getEmail();
        } else {
            throw new RuntimeException("No se pudo extraer el email del token.");
        }
    }
    // AspiranteController.java

    @PutMapping("/admin/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> editAspirante(@PathVariable Integer id, @RequestBody AspiranteDTO aspiranteDTO) {
        try {
            AspiranteDTO updatedAspirante = aspiranteService.editAspirante(id, aspiranteDTO);
            return ResponseEntity.ok(updatedAspirante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing aspirante: " + e.getMessage());
        }
    }

    // AspiranteController.java
    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> deleteAspirante(@PathVariable Integer id) {
        try {
            aspiranteService.deleteAspirante(id);
            return ResponseEntity.ok("Aspirante deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting aspirante: " + e.getMessage());
        }
    }

    @GetMapping("/cohorte/{cohorteId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<AspiranteDTO>> getAspirantesByCohorte(@PathVariable Integer cohorteId) {
        List<AspiranteDTO> aspirantes = aspiranteService.getAspirantesByCohorte(cohorteId);
        return ResponseEntity.ok(aspirantes);
    }


    /**
     * Cambia el campo es_egresado_ufps a false.
     *
     * @param aspiranteId El id del aspirante al que se le va a cambiar es_egresado_ufps.
     * @return Un ResponseEntity con un mensaje indicando si se pudo cambiar o no es_egresado_ufps.
     */
    @PostMapping("/cambiarEsEgresado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> cambiarEsEgresado(@RequestParam Integer aspiranteId) {
        try {
            boolean updated = aspiranteService.cambiarEsEgresado(aspiranteId);
            if (updated) {
                return ResponseEntity.ok("El campo es_egresado_ufps ha sido cambiado a false.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("No se pudo cambiar el campo es_egresado_ufps.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cambiar el campo es_egresado_ufps: " + e.getMessage());
        }
    }

    /**
     * Rechaza la admisión de un aspirante.
     *
     * @param aspiranteId El ID del aspirante cuya admisión se desea rechazar.
     * @return Un ResponseEntity con un mensaje indicando si se pudo rechazar la admisión o no.
     */
    @PostMapping("/rechazarAdmision")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> rechazarAdmision(@RequestParam Integer aspiranteId) {
        try {
            boolean rejected = aspiranteService.rechazarAdmision(aspiranteId);
            if (rejected) {
                return ResponseEntity.ok("La admisión del aspirante ha sido rechazada.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("No se pudo rechazar la admisión.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al rechazar la admisión: " + e.getMessage());
        }
    }

    @GetMapping("/aspirante/{aspiranteId}/calificaciones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> obtenerCalificaciones(@PathVariable Integer aspiranteId) {
        try {
            AspiranteDTO aspirante = aspiranteService.getAspiranteById(aspiranteId); // Fetch aspirant details
            if (aspirante == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aspirante no encontrado.");
            }

            // Map aspirant scores to the CalificacionesResponse DTO
            CalificacionesResponse calificaciones = new CalificacionesResponse();
            calificaciones.setPuntajeNotas(aspirante.getPuntajeNotas());
            calificaciones.setPuntajeDistincionesAcademicas(aspirante.getPuntajeDistincionesAcademicas());
            calificaciones.setPuntajeExperienciaLaboral(aspirante.getPuntajeExperienciaLaboral());
            calificaciones.setPuntajePublicaciones(aspirante.getPuntajePublicaciones());
            calificaciones.setPuntajeCartasReferencia(aspirante.getPuntajeCartasReferencia());
            calificaciones.setPuntajeEntrevista(aspirante.getPuntaje_entrevista());
            calificaciones.setPuntajePrueba(aspirante.getPuntaje_prueba());
            calificaciones.setTotal(aspirante.getTotal());

            return ResponseEntity.ok(calificaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener las calificaciones: " + e.getMessage());
        }
    }

    @GetMapping("/cohorte/{cohorteId}/historicos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> obtenerAspirantesHistoricos(@PathVariable Integer cohorteId) {
        try {
            // Call the service to get historical aspirants
            List<AspiranteDTO> historicos = aspiranteService.obtenerAspirantesHistoricosCohorte(cohorteId);

            // Return the list of historical aspirants
            return ResponseEntity.ok(historicos);
        } catch (Exception e) {
            // Handle any errors that may occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener aspirantes históricos: " + e.getMessage());
        }
    }


    @PostMapping("/aspirante/{aspiranteId}/admitir")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> admitirAspirante(@PathVariable Integer aspiranteId) {
        try {
            // Call the service to admit the aspirant
            aspiranteService.admitirAspirante(aspiranteId);

            // Return a success message
            return ResponseEntity.ok("El aspirante ha sido admitido exitosamente.");
        } catch (Exception e) {
            // Handle any errors that may occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al admitir al aspirante: " + e.getMessage());
        }
    }


    @PostMapping("/aspirante/calificarPrueba")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> calificarPrueba(@RequestBody AspiranteEntrevistaRequest aspiranteEntrevistaRequest) {
        try {
            // Call the service to grade the aspirant's exam
            aspiranteService.calificarPruebaAspirante(aspiranteEntrevistaRequest.getId(), aspiranteEntrevistaRequest.getPuntaje_entrevista());

            // Return a success message
            return ResponseEntity.ok("La prueba del aspirante fue calificada exitosamente.");
        } catch (Exception e) {
            // Handle any errors that may occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al calificar la prueba del aspirante: " + e.getMessage());
        }
    }


    /**
     * Obtiene el usuario del aspirante
     *
     * @param id ID del aspirante
     * @return el usuario que le pertenece al aspirante.
     */
    @GetMapping("/{id}/usuario")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<UserDTO> getUserByAspirante(@PathVariable Integer id) {
        try {
            UserDTO user = aspiranteService.getUserByAspirante(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Desactiva un aspirante por su correo electrónico.
     *
     * @param email El correo del aspirante a desactivar.
     */
    @PutMapping("/disable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> disableAspirante(@RequestParam String email) {
        try {
            aspiranteService.disableAspirante(email);
            return ResponseEntity.ok("Aspirante desactivado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desactivar el aspirante: " + e.getMessage());
        }
    }

    /**
     * Activa un aspirante por su correo electrónico.
     *
     * @param email El correo del aspirante a activar.
     */
    @PutMapping("/enable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> enableAspirante(@RequestParam String email) {
        try {
            aspiranteService.enableAspirante(email);
            return ResponseEntity.ok("Aspirante activado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al activar el aspirante: " + e.getMessage());
        }
    }

    /**
     * Obtiene el aspirante a través del email del usuario.
     *
     * @param email El email del usuario.
     */
    @GetMapping("/byEmail")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AspiranteDTO> getAspiranteByEmail(@RequestParam String email) {
        try {
            AspiranteDTO aspiranteDTO = aspiranteService.getAspiranteByEmail(email);
            return ResponseEntity.ok(aspiranteDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene el aspirante a través del ID del aspirante.
     *
     * @param aspiranteId ID del aspirante.
     */
    @GetMapping("/{aspiranteId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AspiranteDTO> getAspiranteById(@PathVariable Integer aspiranteId) {
        try {
            AspiranteDTO aspiranteDTO = aspiranteService.getAspiranteByAspiranteId(aspiranteId);
            return ResponseEntity.ok(aspiranteDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene la lista de aspirantes para la cohorte actual.
     */
    @GetMapping("/cohorte/actual")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<AspiranteDTO>> listarAspirantesCohorteActual() {
        try {
            List<AspiranteDTO> aspirantes = aspiranteService.listarAspirantesCohorteActual();
            return ResponseEntity.ok(aspirantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Habilita la fecha de entrevista para un aspirante.
     *
     * @param id               ID del aspirante.
     * @param fechaEntrevista  Fecha de la entrevista.
     */
    @PutMapping("/habilitarFechaEntrevista")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> habilitarFechaEntrevista(@RequestParam Integer id, @RequestParam LocalDateTime fechaEntrevista) {
        try {
            aspiranteService.habilitarFechaEntrevista(id, fechaEntrevista);
            return ResponseEntity.ok("Fecha de entrevista habilitada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al habilitar la fecha de entrevista: " + e.getMessage());
        }
    }
}
