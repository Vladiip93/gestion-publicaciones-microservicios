package com.espe.mspr.catalogoservice.controllers;

import com.espe.mspr.catalogoservice.models.CatalogoPublicacion;
import com.espe.mspr.catalogoservice.repositories.CatalogoPublicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoPublicacionRepository repo;

    // Soporta /api/catalogo  y /api/catalogo/
    @GetMapping({"", "/"})
    public Page<CatalogoPublicacion> list(@RequestParam(name = "q", required = false) String q,
                                          Pageable pageable) {
        if (q == null || q.isBlank()) {
            return repo.findAll(pageable);
        }
        return repo.search(q, pageable);
    }
}
