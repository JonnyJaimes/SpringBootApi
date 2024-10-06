package com.ufps.maestria.controllers;

import com.ufps.maestria.dto.NotificacionDTO;
import com.ufps.maestria.models.AspiranteEntity;
import com.ufps.maestria.payload.response.NotificacionResponse;
import com.ufps.maestria.repository.AspiranteRepository;
import com.ufps.maestria.services.implementations.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/notificacion")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private AspiranteRepository aspiranteRepository;

    /**
     * Endpoint para listar las notificaciones de un aspirante y devolverlas como
     * respuesta.
     *
     * @return una lista de objetos NotificacionResponse que contiene la información
     *         de las notificaciones
     */
    @PreAuthorize("ROLE_USUARIO")
    @GetMapping("/listar")
    public List<NotificacionResponse> listarNotificacionesAspirante() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        AspiranteEntity aspiranteEntity = aspiranteRepository.findByUser_Email(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe ningún aspirante asociado al email"));

        List<NotificacionDTO> notificacionesDTO = notificacionService.listarNotificaciones(aspiranteEntity.getId());
        List<NotificacionResponse> notificacionesResponse = new ArrayList<>();

        for (NotificacionDTO notificacionDTO : notificacionesDTO) {
            NotificacionResponse notificacionResponse = new NotificacionResponse();
            notificacionResponse.setEnunciado(notificacionDTO.getEnunciado());
            notificacionResponse.setEstado(notificacionDTO.getEstado());
            notificacionResponse.setFecha_envio(notificacionDTO.getFecha_envio());

            notificacionesResponse.add(notificacionResponse);
        }
        return notificacionesResponse;
    }



    /**
     * Marca las notificaciones de un usuario como leídas.
     *
     * @return Una respuesta de tipo ResponseEntity<String>.
     * @throws UsernameNotFoundException si no se encuentra un aspirante asociado al
     *                                   usuario.
     */
    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/checkRead")
    public ResponseEntity<String> marcarNotificaciones() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Use getName() to obtain the username or email

        AspiranteEntity aspiranteEntity = aspiranteRepository.findByUser_Email(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe ningún aspirante asociado al email"));

        notificacionService.marcarComoLeido(aspiranteEntity);
        return ResponseEntity.ok("Notificaciones marcadas como leídas correctamente");
    }



    /**
     * Metodo para formatear la fecha
     * @param fecha
     * @return
     */
    private String formatearFecha(LocalDateTime fecha) {
        Locale locale = Locale.getDefault();
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, locale);
        int dia = fecha.getDayOfMonth();
        int anio = fecha.getYear();
        int hora = fecha.getHour();
        int minuto = fecha.getMinute();
        int segundo = fecha.getSecond();

        return diaSemana + " " + dia + " de " + mes + " de " + anio + " a las " + hora + ":" + minuto + ":" + segundo;
    }
}