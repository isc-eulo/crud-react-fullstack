package com.clientes.react.service.validacion;

import com.clientes.react.dominio.entity.Cliente;
import com.clientes.react.dominio.facade.ClientesFacade;
import com.clientes.react.exception.BdaException;
import com.clientes.react.exception.NegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ClienteValidacion {
    private final ClientesFacade clientesFacade;

    /**
     * Valida si la identificación ya existe.
     * Si existe, dispara error; si no, devuelve la entidad original.
     */
    public Mono<Cliente> validarIdentificacionUnica(Cliente entidad) {
        return clientesFacade.buscarPorIdentificacion(entidad.getIdentificacion())
                // BLOQUE 2: Error de consulta -> Fallo de conexión o sintaxis en BD
                .onErrorMap(e -> new BdaException("Error al consultar existencia en BD", HttpStatus.SERVICE_UNAVAILABLE))
                // SI ENCUENTRA: El flujo tiene datos, entonces disparamos error de Negocio
                .flatMap(existente -> Mono.<Cliente>error(
                        new NegocioException("Regla de Negocio: El identificador ya existe", HttpStatus.BAD_REQUEST)))
                // SI NO ENCUENTRA: Regresamos la entidad para que el Service siga
                .switchIfEmpty(Mono.just(entidad));
    }


    /**
     * Valida que la identificación no sea usada por OTRO cliente.
     */
    public Mono<Cliente> validarIdentificacionParaActualizar(Cliente entidad, Long idActual) {
        return clientesFacade.buscarPorIdentificacion(entidad.getIdentificacion())
                // BLOQUE: Error de consulta en BD
                .onErrorMap(e -> new BdaException("Error al consultar existencia en BD", HttpStatus.SERVICE_UNAVAILABLE))
                // Filtramos: ¿El que encontré tiene un ID diferente al que estoy editando?
                .filter(existente -> !existente.getId().equals(idActual))
                // Si el filtro arroja algo, es porque otro ya tiene ese DNI
                .flatMap(otro -> Mono.<Cliente>error(
                        new NegocioException("La identificación ya pertenece a otro cliente", HttpStatus.CONFLICT)))
                // Si no hay conflicto, devolvemos la entidad para seguir
                .switchIfEmpty(Mono.just(entidad));
    }
}
