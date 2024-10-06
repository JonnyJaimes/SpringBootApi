package com.ufps.maestria.repository;


import com.ufps.maestria.models.EstadoDocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository que define los métodos para acceder al estado de los documentos del Aspirante.
 * @author Angel Yesid Duque Cruz
 */
@Repository
public interface EstadoDocRepository extends JpaRepository<EstadoDocEntity, Integer> {
    EstadoDocEntity findByNombre(String nuevoEstado);
}
