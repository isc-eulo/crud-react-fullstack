package com.clientes.react.remote.remote;

import com.clientes.react.remote.model.PokemonModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

public interface PokeApiClient {

    @GetExchange("/pokemon/{name}")
    Mono<PokemonModel> getPokemonByName(@PathVariable("name") String name);
}
