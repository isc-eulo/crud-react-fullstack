package com.example.crudreac.dominio.repository;

import com.example.crudreac.dominio.entity.Producto;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductosRepository extends ReactiveCrudRepository<Producto, Long> {

    Flux<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Método necesario para buscar una lista de productos por el ID de la categoría.
    Flux<Producto> findByCategoriaId(Long categoriaId);

    // Método para buscar productos por ID de categoría Y precio unitario (necesario para el segundo nuevo método)
    Flux<Producto> findByCategoriaIdAndPrecioUnitario(Long categoriaId, double precioUnitario);
}
