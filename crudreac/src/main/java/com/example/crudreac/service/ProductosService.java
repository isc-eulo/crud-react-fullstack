package com.example.crudreac.service;

import com.example.crudreac.dominio.entity.Producto;
import com.example.crudreac.dominio.repository.ProductosRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class ProductosService {

    private  final ProductosRepository repository;

    /**
     * Crea un nuevo producto, convirtiendo el DTO a Entidad antes de persistir.
     */
    public Mono<Producto> crearProducto(Producto producto){
    return this.repository.save(producto);

    }

    /**
     * Obtiene un producto por su ID.
     */
    public Mono<Producto> obtenerPorId(Long identificar){
        return this.repository.findById(identificar);
    }

    /**
     * Obtiene un producto por su ID.
     */
    public Flux<Producto> obtenerTodos(){

        return this.repository.findAll();
    }

    /**
     * Actualiza un producto existente (se usa ProductoCreacionDTO para los datos de entrada).
     */
    /**
     * Actualiza un producto existente.
     */
    public Mono<Producto> actualizar(Long id, Producto productoDetalles){
        return this.repository.findById(id)
                // Cuando se encuentra el producto existente (productoExistente)
                .flatMap(productoExistente -> {
                    // Copiamos el ID del existente al objeto que vamos a guardar
                    // para asegurar que es una actualización y no una inserción
                    productoDetalles.setId(id);

                    // Guardamos el objeto actualizado, devolviendo el Mono<Producto>
                    return this.repository.save(productoDetalles);
                })
                // Si findById no encuentra nada, lanza una excepción (se devuelve el error)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con ID: " + id)));
    }
}
