package com.clientes.react.remote;

import com.clientes.react.remote.remote.PokeApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RemotesConfig {

    @Bean
    public PokeApiClient pokeApiClient(WebClient.Builder builder) {
        WebClient webClient = builder.baseUrl("https://pokeapi.co/api/v2").build();

        // Forma moderna (Spring Boot 3.2+)
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build()
                .createClient(PokeApiClient.class);
    }
}
