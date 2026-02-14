package com.example.crudreac.dominio.repository;

import com.example.crudreac.dominio.entity.Categoria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CategoriasRepository extends ReactiveCrudRepository<Categoria, Long> {

    Mono<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
