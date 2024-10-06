package com.ufps.maestria.repository;


import com.ufps.maestria.models.TipoDocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository que define los métodos para acceder al tipo de documento.
 * @author Angel Yesid Duque Cruz, Miguel Angel Lara, Gibson Arbey
 */
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumentoEntity, Integer> {



    /**
     * Buscar tipo de documento por ID.
     *
     * @param id el ID del tipo de documento a buscar
     * @return un objeto Optional que puede contener un TipoDocumentoEntity correspondiente al ID proporcionado,
     * o puede estar vacío si no se encuentra ningún tipo de documento.
     */
    Optional<TipoDocumentoEntity> findById(Integer id);

    /**
     * Buscar tipo de documento por nombre.
     *
     * @param nombre el nombre del tipo de documento a buscar
     * @return un objeto Optional que puede contener un TipoDocumentoEntity correspondiente al nombre proporcionado,
     * o puede estar vacío si no se encuentra ningún tipo de documento.
     */
    Optional<TipoDocumentoEntity> findByNombre(String nombre);


}