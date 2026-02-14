package com.clientes.react.remote;

import com.clientes.react.remote.model.PokemonModel;
import com.clientes.react.remote.remote.PokeApiClient;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
public class PokemonRemoteService {

    private final PokeApiClient pokeApiClient;
    private final ReactiveCircuitBreaker circuitBreaker;

    // Cambia el parámetro de 'ReactiveResilience4JCircuitBreakerFactory' a 'ReactiveCircuitBreakerFactory'
    public PokemonRemoteService(PokeApiClient pokeApiClient, ReactiveCircuitBreakerFactory factory) {
        this.pokeApiClient = pokeApiClient;
        // Ahora sí va a reconocer el método .create()
        this.circuitBreaker = factory.create("pokeapi");
    }
    /**
     * Intenta obtener un Pokemon de la API externa con protección de Circuit Breaker.
     * @param nombre Nombre del pokemon a buscar.
     * @return Un Mono que emitirá el modelo del Pokemon o un objeto de fallback si falla.
     */
    public Mono<PokemonModel> obtenerPokemonConResiliencia(String nombre) {
        // 1. Llamada al cliente HTTP: Se inicia la petición asíncrona.
        // El .toLowerCase() asegura que la URL sea válida para la PokeAPI.
        return pokeApiClient.getPokemonByName(nombre.toLowerCase())

                // 2. .transform(): Este operador permite "envolver" todo el flujo anterior
                // dentro de la lógica del Circuit Breaker sin romper la cadena reactiva.
                .transform(it ->
                        // 3. circuitBreaker.run(flujo, fallback):
                        // - Si el circuito está CERRADO: Intenta ejecutar 'it' (la llamada a la API).
                        // - Si la API falla o el circuito está ABIERTO: Ejecuta automáticamente la función de error.
                        circuitBreaker.run(it, throwable -> fallbackPokemon(nombre, throwable))
                );
    }

    /**
     * Método de Fallback (Respaldo).
     * Se ejecuta en tres casos:
     * a) La API responde con error (4xx, 5xx).
     * b) Hay un timeout (la API tarda mucho).
     * c) El circuito está abierto (muchas fallas previas).
     */
    private Mono<PokemonModel> fallbackPokemon(String nombre, Throwable e) {
        // 1. Creamos una instancia local de PokemonModel para no devolver un error al cliente.
        PokemonModel model = new PokemonModel();

        // 2. Seteamos valores por defecto o "dummy" para indicar el estado del error.
        model.setId(0);
        model.setName(nombre + " (No disponible)"); // Informamos qué nombre falló.
        model.setWeight(0);

        // 3. Mono.just(): Envuelve el objeto en un contenedor reactivo.
        // Esto permite que el flujo continúe su camino sin "explotar" con una excepción.
        return Mono.just(model);
    }
}


