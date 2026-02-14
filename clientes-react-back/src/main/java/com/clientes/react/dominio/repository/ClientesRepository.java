package com.clientes.react.dominio.repository;

import com.clientes.react.dominio.entity.Cliente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClientesRepository extends ReactiveCrudRepository<Cliente, Long> {

    // Al ser reactivo, "lista" se traduce en un Flux
    // Un Flux emite 0 a N elementos de forma asíncrona
    Flux<Cliente> findByEmail(String email);

    // Para una búsqueda por identificador único, usamos Mono
    // Un Mono emite 0 o 1 elemento
    Mono<Cliente> findByIdentificacion(String identificacion);
}