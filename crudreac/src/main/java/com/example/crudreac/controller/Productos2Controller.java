package com.example.crudreac.controller;
import com.example.crudreac.dominio.entity.Producto;
import com.example.crudreac.dto.ProductoCreacionDTO;
import com.example.crudreac.dto.ProductoRespuestaDTO;
import com.example.crudreac.service.Productos2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController // Define la clase como un controlador REST
@RequestMapping("/api/productos/v2") // Ruta base para todos los endpoints
@RequiredArgsConstructor // Inyección del servicio vía constructor (gracias a Lombok)
@Tag(name = "Productos-version-2", description = "Gestión de productos de inventario de forma reactiva") // Documentación Swagger
@CrossOrigin(origins = "http://localhost:8080")
public class Productos2Controller {

    private final Productos2Service service;

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
    public Mono<ProductoRespuestaDTO> crearProducto(@RequestBody ProductoCreacionDTO
                                                                producto) {
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
    @GetMapping(produces = {
            MediaType.TEXT_EVENT_STREAM_VALUE // ¡Esto es correcto!
    })
    @ResponseStatus(HttpStatus.OK)
    /*
    Para que el retardo natural de la aplicación (el tiempo que tarda la DB en entregar el millón de registros, el mapeo y la latencia de red) sea visible en el navegador como un streaming incremental,
    el endpoint de WebFlux debe configurarse como TEXT_EVENT_STREAM_VALUE
     delayElements(Duration.ofMillis(3000)) esta es solo para probar la lactencia
     */
    public Flux<ProductoRespuestaDTO> obtenerTodos() {
        return service.obtenerTodosLosProductos()
                // Agregamos un retardo artificial de 300ms entre la emisión de cada producto
                .delayElements(Duration.ofMillis(3000));
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
    public Mono<ProductoRespuestaDTO> obtenerPorId(@PathVariable("id") Long id) {
        // En WebFlux, si el Mono está vacío (producto no existe), Spring
        // automáticamente devuelve un 404 Not Found.
        return service.obtenerProductoPorId(id);
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
    public Mono<ProductoRespuestaDTO> actualizarProducto(
            @PathVariable("id") Long id,
            @RequestBody ProductoCreacionDTO producto
    ) {
        // El servicio maneja la lógica de buscar, actualizar y lanzar el error 404 (implícitamente)
        return service.actualizarProducto(id,producto);
    }


    // --- ENDPOINTS DE BÚSQUEDA ---

    /**
     * Endpoint para buscar productos por el nombre exacto de su categoría.
     * URL: GET /api/productos/buscar/categoria?nombre={nombreCategoria}
     *
     * @param nombreCategoria El nombre de la categoría (pasado como parámetro de consulta).
     * @return Flux<ProductoRespuestaDTO> - Flujo de productos que coinciden.
     */
    @GetMapping("/buscar/categoria")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductoRespuestaDTO> buscarPorNombreCategoria(
            @RequestParam(name = "nombre") String nombreCategoria) {

        // Llama al método del servicio que devuelve un Flux y lo retorna directamente.
        // Spring WebFlux se encarga de serializar el flujo de datos al cliente.
        return service.obtenerProductosPorNombreCategoria(nombreCategoria);
    }

    /**
     * Endpoint para buscar productos por nombre de categoría Y precio unitario exacto.
     * URL: GET /api/productos/buscar/precio?categoria={nombreCategoria}&precio={precio}
     *
     * @param nombreCategoria El nombre de la categoría (parámetro de consulta).
     * @param precioUnitario El precio exacto del producto (parámetro de consulta).
     * @return Flux<ProductoRespuestaDTO> - Flujo de productos que coinciden con ambos criterios.
     */
    @GetMapping("/buscar/precio")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductoRespuestaDTO> buscarPorCategoriaYPrecio(
            @RequestParam(name = "categoria") String nombreCategoria,
            @RequestParam(name = "precio") double precioUnitario) {

        // Llama al método del servicio que combina los criterios de búsqueda en la DB.
        return service.obtenerProductosPorCategoriaYPrecio(nombreCategoria, precioUnitario);
    }
}