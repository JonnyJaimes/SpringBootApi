package com.bezkoder.springjwt.services.interfaces;



import com.bezkoder.springjwt.dto.NotificacionDTO;
import com.bezkoder.springjwt.models.AspiranteEntity;

import java.util.List;

public interface NotificacionServiceInterface {

    List<NotificacionDTO> listarNotificaciones(Integer aspiranteId);

    void crearNotificacion(String mensaje, Integer aspiranteId);

    void marcarComoLeido(AspiranteEntity aspiranteEntity);
}
