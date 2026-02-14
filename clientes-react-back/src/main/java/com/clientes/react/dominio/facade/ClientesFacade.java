package com.clientes.react.dominio.facade;

import com.clientes.react.dominio.entity.Cliente;
import com.clientes.react.dominio.repository.ClientesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ClientesFacade {

    private final ClientesRepository repository;

    // --- BÚSQUEDAS ---

    public Flux<Cliente> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public Mono<Cliente> buscarPorIdentificacion(String identificacion) {
        return repository.findByIdentificacion(identificacion);
    }

    public Flux<Cliente> obtenerTodos() {
        return repository.findAll();
    }

    public Mono<Cliente> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    // --- OPERACIONES CRUD CON TRANSACCIONALIDAD ---

    @Transactional
    public Mono<Cliente> crear(Cliente cliente) {
        // En R2DBC, save() funciona para insertar si el ID es nulo
        return repository.save(cliente);
    }

    @Transactional
    public Mono<Cliente> actualizar(Long id, Cliente cliente) {
        return repository.findById(id)
                .flatMap(clienteExistente -> {
                    clienteExistente.setNombre(cliente.getNombre());
                    clienteExistente.setIdentificacion(cliente.getIdentificacion());
                    clienteExistente.setEmail(cliente.getEmail());
                    return repository.save(clienteExistente);
                });
    }

    @Transactional
    public Mono<Void> eliminar(Long id) {
        return repository.deleteById(id);
    }
}