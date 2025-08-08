package com.espe.mspr.catalogoservice.repositories;

import com.espe.mspr.catalogoservice.models.CatalogoPublicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CatalogoPublicacionRepository extends JpaRepository<CatalogoPublicacion, UUID> {

    // Útil si prefieres no usar findById
    Optional<CatalogoPublicacion> findByPublicacionId(UUID publicacionId);

    Page<CatalogoPublicacion> findByTipoIgnoreCase(String tipo, Pageable pageable);

    @Query("""
           SELECT DISTINCT c
           FROM CatalogoPublicacion c
           LEFT JOIN c.palabras p
           WHERE (:q IS NULL OR :q = '' OR
                 LOWER(c.titulo)  LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(c.resumen) LIKE LOWER(CONCAT('%', :q, '%')) OR
                 LOWER(p)         LIKE LOWER(CONCAT('%', :q, '%')))
           """)
    Page<CatalogoPublicacion> search(@Param("q") String q, Pageable pageable);
}
