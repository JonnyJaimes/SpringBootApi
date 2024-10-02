package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.EstadoHistorialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoHistorialRepository extends JpaRepository<EstadoHistorialEntity, Integer> {
}
