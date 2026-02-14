package com.example.crudreac.controller;
import com.example.crudreac.dominio.entity.Producto;
import com.example.crudreac.service.ProductosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController // Define la clase como un controlador REST
@RequestMapping("/api/productos") // Ruta base para todos los endpoints
@RequiredArgsConstructor // Inyección del servicio vía constructor (gracias a Lombok)
@Tag(name = "Productos", description = "Gestión de productos de inventario de forma reactiva") // Documentación Swagger
public class ProductosController {

    private final ProductosService service;

    // -------------------------------------------------------------------
    // 1. CREAR PRODUCTO (POST)
    // -------------------------------------------------------------------

    @Operation(
            summary = "Crea un nuevo producto",
            description = "Persiste un nuevo producto en la base de datos",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Producto creado exitosamente",
                            content = @Content(schema = @Schema(implementation = Producto.class))
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Devuelve código 201
    public Mono<Producto> crearProducto(@RequestBody Producto producto) {
        return service.crearProducto(producto);
    }

    // -------------------------------------------------------------------
    // 2. OBTENER TODOS (GET)
    // -------------------------------------------------------------------

    @Operation(
            summary = "Obtiene todos los productos",
            description = "Devuelve un flujo (Flux) de todos los productos en el inventario",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de productos devuelta",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Producto.class)))
                    )
            }
    )
    @GetMapping
    public Flux<Producto> obtenerTodos() {
        return service.obtenerTodos();
    }

    // -------------------------------------------------------------------
    // 3. OBTENER POR ID (GET)
    // -------------------------------------------------------------------

    @Operation(
            summary = "Obtiene un producto por ID",
            description = "Busca y devuelve un producto específico por su identificador",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Producto encontrado",
                            content = @Content(schema = @Schema(implementation = Producto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @GetMapping("/{id}")
    public Mono<Producto> obtenerPorId(@PathVariable("id") Long id) {
        // En WebFlux, si el Mono está vacío (producto no existe), Spring
        // automáticamente devuelve un 404 Not Found.
        return service.obtenerPorId(id);
    }

    // -------------------------------------------------------------------
    // 4. ACTUALIZAR PRODUCTO (PUT)
    // -------------------------------------------------------------------

    @Operation(
            summary = "Actualiza un producto existente",
            description = "Reemplaza completamente los datos de un producto por su ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Producto actualizado exitosamente",
                            content = @Content(schema = @Schema(implementation = Producto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @PutMapping("/{id}")
    public Mono<Producto> actualizarProducto(
            @PathVariable("id") Long id,
            @RequestBody Producto producto
    ) {
        // El servicio maneja la lógica de buscar, actualizar y lanzar el error 404 (implícitamente)
        return service.actualizar(id, producto);
    }


}