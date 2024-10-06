package com.ufps.maestria.services.interfaces;



import com.ufps.maestria.dto.NotificacionDTO;
import com.ufps.maestria.models.AspiranteEntity;

import java.util.List;

public interface NotificacionServiceInterface {

    List<NotificacionDTO> listarNotificaciones(Integer aspiranteId);

    void crearNotificacion(String mensaje, Integer aspiranteId);

    void marcarComoLeido(AspiranteEntity aspiranteEntity);
}
