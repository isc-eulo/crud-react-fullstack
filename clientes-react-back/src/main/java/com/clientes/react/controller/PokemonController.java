package com.clientes.react.controller;

import com.clientes.react.remote.PokemonRemoteService;
import com.clientes.react.remote.model.PokemonModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pokemon")
@RequiredArgsConstructor // Lombok: Inyecta automáticamente el PokemonRemoteService
public class PokemonController {

    private final PokemonRemoteService pokemonRemoteService;

    /**
     * Endpoint para obtener un solo pokemon por nombre.
     * Ejemplo: GET /api/v1/pokemon/pikachu
     */
    @GetMapping("/{nombre}")
    public Mono<PokemonModel> buscarPorNombre(@PathVariable String nombre) {
        // Llamamos al servicio que ya tiene el Circuit Breaker configurado
        return pokemonRemoteService.obtenerPokemonConResiliencia(nombre);
    }

}
