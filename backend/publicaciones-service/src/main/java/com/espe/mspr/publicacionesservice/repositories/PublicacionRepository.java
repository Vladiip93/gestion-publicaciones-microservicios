package com.espe.mspr.publicacionesservice.repositories;

import com.espe.mspr.publicacionesservice.models.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, UUID> {}
