package com.clientes.react.controller;

import com.clientes.react.dto.ClienteRequestDTO;
import com.clientes.react.dto.ClienteResponseDTO;
import com.clientes.react.service.ClientesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para la gestión reactiva de clientes.
 * Provee endpoints para operaciones CRUD utilizando Project Reactor.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class ClientesController {

    private final ClientesService clientesService;

    /**
     * Registra un nuevo cliente en el sistema.
     * * @param dto Objeto con la información del cliente a crear.
     * @return Mono con el DTO del cliente creado y estado 201.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ClienteResponseDTO> crear(@RequestBody ClienteRequestDTO dto) {
        return clientesService.guardarNuevo(dto);
    }

    /**
     * Recupera la lista completa de clientes registrados.
     * * @return Flux que emite los clientes de forma asíncrona.
     */
    @GetMapping
    public Flux<ClienteResponseDTO> listarTodos() {
        return clientesService.listarTodos();
    }

    /**
     * Obtiene los detalles de un cliente mediante su identificador único.
     * * @param id Identificador primario del cliente.
     * @return Mono con la información del cliente encontrado.
     */
    @GetMapping("/{id}")
    public Mono<ClienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return clientesService.buscarPorId(id);
    }

    /**
     * Actualiza la información de un cliente existente.
     * * @param id  Identificador del cliente a modificar.
     * @param dto Nuevos datos para el cliente.
     * @return Mono con el cliente actualizado.
     */
    @PatchMapping("/{id}")
    public Mono<ClienteResponseDTO> actualizar(@PathVariable Long id, @RequestBody ClienteRequestDTO dto) {
        return clientesService.actualizarCliente(id, dto);
    }

    /**
     * Elimina un cliente del sistema de forma permanente.
     * * @param id Identificador del cliente a remover.
     * @return Mono vacío que indica la finalización de la operación.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable Long id) {
        return clientesService.eliminarCliente(id);
    }

    /**
     * Filtra clientes por su dirección de correo electrónico.
     * * @param email Correo electrónico a consultar.
     * @return Flux con los clientes que coincidan con el criterio.
     */
    @GetMapping("/busqueda")
    public Flux<ClienteResponseDTO> buscarPorEmail(@RequestParam String email) {
        return clientesService.listarPorEmail(email);
    }
}