package com.ufps.maestria.repository;

import com.ufps.maestria.models.EstadoHistorialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoHistorialRepository extends JpaRepository<EstadoHistorialEntity, Integer> {
}
