package com.ufps.maestria.repository;


import com.ufps.maestria.models.AspiranteEntity;
import com.ufps.maestria.models.CohorteEntity;
import com.ufps.maestria.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Repository que define los métodos para acceder a los datos del Aspirante.
 * @author Angel Yesid Duque Cruz, Julian Camilo Riveros Fonseca, Gibson Arbey, Juan Pablo Correa Tarazona
 */
@Repository
public interface AspiranteRepository extends JpaRepository<AspiranteEntity, Integer> {

    // Correct method name to match field in AspiranteEntity
    Optional<AspiranteEntity> findByCorreoPersonal(String correoPersonal);

    // Other methods...
    Optional<AspiranteEntity> findByUser_Id(Integer user_id);

    Optional<AspiranteEntity> findById(Integer id);

    AspiranteEntity findByUser(User user);

    List<AspiranteEntity> findByCohorte(CohorteEntity cohorte);

    Optional<AspiranteEntity> findByUser_Email(String correoPersonal);

    List<AspiranteEntity> findByCohorteId(Integer cohorteId);
}

