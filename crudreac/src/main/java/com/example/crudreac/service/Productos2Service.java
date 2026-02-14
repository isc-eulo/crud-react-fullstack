package com.example.crudreac.service;


import com.example.crudreac.dominio.entity.Categoria;
import com.example.crudreac.dominio.entity.Producto;
import com.example.crudreac.dominio.repository.CategoriasRepository;
import com.example.crudreac.dominio.repository.ProductosRepository;
import com.example.crudreac.dto.CategoriaRequestDto;
import com.example.crudreac.dto.ProductoCreacionDTO;
import com.example.crudreac.dto.ProductoRespuestaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

/**
 * Servicio Reactivo para la gestión de productos.
 * Contiene la lógica de negocio y las conversiones entre DTOs y entidades.
 */
@Service
@RequiredArgsConstructor
public class Productos2Service {

    // Inyección de dependencias para los repositorios de Producto y Categoria
    private final ProductosRepository productoRepository;
    private final CategoriasRepository categoriaRepository;


    // --- MÉTODOS DE CONVERSIÓN ---

    /**
     * Convierte un DTO de Creación a una entidad Producto.
     * @param dto El DTO de entrada.
     * @param categoriaId El ID de la categoría asociada, resuelto previamente.
     * @return La entidad Producto lista para ser guardada.
     */
    private Producto toEntity(ProductoCreacionDTO dto, Long categoriaId) {
        // Crea y devuelve una nueva instancia de la entidad Producto
        return new Producto(
                null,                           // El ID es null, será autogenerado por la base de datos
                dto.getNombre(),                // Asigna el nombre del DTO
                categoriaId,                    // Asigna el ID de la categoría (Long)
                dto.getPrecioUnitario(),        // Asigna el precio unitario
                dto.getStock()                  // Asigna el stock
        );
    }

    /**
     * Convierte una entidad Producto a un DTO de Respuesta.
     * @param entity La entidad Producto leída de la base de datos.
     * @param categoria La entidad Categoria asociada.
     * @return El DTO de Respuesta completo.
     */
    private ProductoRespuestaDTO toRespuestaDto(Producto entity, Categoria categoria) {
        // Crea una nueva instancia del DTO de Respuesta
        ProductoRespuestaDTO dto = new ProductoRespuestaDTO();
        // Asigna el ID de la entidad
        dto.setId(entity.getId());
        // Asigna los campos de la clase base (ProductoBaseDTO)
        dto.setNombre(entity.getNombre());
        // El campo 'categoria' del ProductoBaseDTO se omite aquí ya que es de solo lectura en la respuesta,
        // pero se incluye en el DTO de Respuesta (ProductoRespuestaDTO)
        dto.setPrecioUnitario(entity.getPrecioUnitario());
        dto.setStock(entity.getStock());

        // Crea el DTO de la categoría anidada (simulando CategoriaRequestDto para la respuesta)
        CategoriaRequestDto categoriaDto = new CategoriaRequestDto();
        categoriaDto.setNombre(categoria.getNombre()); // Asigna el nombre de la categoría
        // El ID de la categoría también se incluye en el DTO de la categoría anidada
        categoriaDto.setId(categoria.getId());

        // Asigna el DTO de la categoría anidada al DTO de ProductoRespuestaDTO
        dto.setCategoriaRequestDto(categoriaDto);

        return dto;
    }

    // --- OPERACIONES CRUD REACTIVAS ---

    /**
     * 💾 Crea un nuevo producto en la base de datos.
     * @param productoCreacionDTO DTO con los datos del nuevo producto.
     * @return Mono que emite el DTO de Respuesta del producto creado (con ID).
     */
    public Mono<ProductoRespuestaDTO> crearProducto(ProductoCreacionDTO productoCreacionDTO) {

        String nombreCategoria = productoCreacionDTO.getCategoria(); // Obtenemos el nombre

        // 1. Busca la entidad Categoria por su nombre
        // Se corrige añadiendo el argumento 'nombreCategoria'
        return categoriaRepository.findByNombreContainingIgnoreCase(nombreCategoria)
                // 2. Transforma el Mono<Categoria> a Mono<Producto>
                .flatMap(categoria -> {
                    // Si se encuentra la categoría, convierte el DTO a Entidad Producto
                    Producto producto = toEntity(productoCreacionDTO, categoria.getId());
                    // 3. Guarda la entidad Producto en la base de datos
                    return productoRepository.save(producto)
                            // 4. Mapea el Producto guardado al DTO de Respuesta, incluyendo la Categoria
                            .map(savedProducto -> toRespuestaDto(savedProducto, categoria));
                })
                // Manejo de error si la categoría no existe
                .switchIfEmpty(Mono.error(new NoSuchElementException("La categoría '" + nombreCategoria + "' no existe.")));
    }


    /**
     * 🔎 Busca un producto por su identificador único (ID).
     * @param id ID del producto a buscar.
     * @return Mono que emite el DTO de Respuesta si se encuentra, o un error si no.
     */
    public Mono<ProductoRespuestaDTO> obtenerProductoPorId(Long id) {
        // 1. Busca la entidad Producto por ID
        return productoRepository.findById(id)
                // 2. Si se encuentra el Producto, usa flatMap para buscar la Categoria
                .flatMap(producto -> categoriaRepository.findById(producto.getCategoriaId())
                        // 3. Una vez que se tienen ambos, Producto y Categoria, mapea a ProductoRespuestaDTO
                        .map(categoria -> toRespuestaDto(producto, categoria))
                        // Manejo si el ID de Categoria en Producto es inválido
                        .switchIfEmpty(Mono.error(new IllegalStateException("Producto con ID " + id + " tiene una Categoria inválida."))))
                // Manejo si el Producto no existe
                .switchIfEmpty(Mono.error(new NoSuchElementException("Producto con ID " + id + " no encontrado.")));
    }

    /**
     * 📜 Obtiene todos los productos disponibles.
     * @return Flux que emite DTOs de Respuesta para cada producto.
     */
    public Flux<ProductoRespuestaDTO> obtenerTodosLosProductos() {
        // 1. Obtiene un Flux de todas las entidades Producto
        return productoRepository.findAll()
                // 2. Usa flatMap para buscar la Categoria de cada Producto de manera reactiva y paralela
                .flatMap(producto -> categoriaRepository.findById(producto.getCategoriaId())
                        // 3. Combina el Producto y su Categoria en el DTO de Respuesta
                        .map(categoria -> toRespuestaDto(producto, categoria))
                        // Si por alguna razón la categoría no existe (debería ser un error de BD), lo omite
                        .onErrorResume(e -> {
                            System.err.println("Error al encontrar la categoría para el producto con ID: " + producto.getId() + ". Error: " + e.getMessage());
                            return Mono.empty(); // Omite este elemento si hay error
                        })
                );
    }

    /**
     * ✏️ Actualiza un producto existente.
     * @param id ID del producto a actualizar.
     * @param dto DTO con los nuevos datos.
     * @return Mono que emite el DTO de Respuesta actualizado, o un error si no existe.
     */
    public Mono<ProductoRespuestaDTO> actualizarProducto(Long id, ProductoCreacionDTO dto) {
        // 1. Primero, busca el producto existente por ID para asegurar que existe
        return productoRepository.findById(id)
                // 2. Si el producto existe, busca la categoría por nombre
                .flatMap(existingProducto -> categoriaRepository.findByNombreContainingIgnoreCase(dto.getCategoria())
                        // 3. Una vez que tenemos la Categoria, realizamos la actualización y guardado
                        .flatMap(categoria -> {
                            // Mapea los campos del DTO a la entidad existente (manteniendo el ID)
                            existingProducto.setNombre(dto.getNombre());
                            existingProducto.setCategoriaId(categoria.getId()); // Actualiza el ID de la categoría
                            existingProducto.setPrecioUnitario(dto.getPrecioUnitario());
                            existingProducto.setStock(dto.getStock());

                            // Guarda la entidad actualizada
                            return productoRepository.save(existingProducto)
                                    // Mapea la entidad guardada al DTO de respuesta, usando la categoría encontrada
                                    .map(updatedProducto -> toRespuestaDto(updatedProducto, categoria));
                        })
                        // Manejo de error si la nueva categoría no existe
                        .switchIfEmpty(Mono.error(new NoSuchElementException("La categoría '" + dto.getCategoria() + "' no existe."))
                        )
                )
                // Manejo de error si el producto original no existe
                .switchIfEmpty(Mono.error(new NoSuchElementException("Producto con ID " + id + " no encontrado para actualizar.")));
    }

    /**
     * 🗑️ Elimina un producto por su ID.
     * @param id ID del producto a eliminar.
     * @return Mono<Void> que se completa cuando la eliminación ha terminado.
     */
    public Mono<Void> eliminarProducto(Long id) {
        // 1. Busca el producto por ID
        return productoRepository.findById(id)
                // 2. Si existe, llama al método de eliminación del repositorio
                .flatMap(producto -> productoRepository.delete(producto))
                // 3. Si no existe, emite un error
                .switchIfEmpty(Mono.error(new NoSuchElementException("Producto con ID " + id + " no encontrado para eliminar.")));
    }

    /**
     * 🔍 Obtiene todos los productos que pertenecen a una categoría específica por su nombre.
     *
     * @param nombreCategoria El nombre descriptivo de la categoría a buscar.
     * @return Flux que emite DTOs de Respuesta para los productos encontrados.
     */
    public Flux<ProductoRespuestaDTO> obtenerProductosPorNombreCategoria(String nombreCategoria) {
        // 1. Inicia buscando la entidad Categoria por su nombre
        return categoriaRepository.findByNombreContainingIgnoreCase(nombreCategoria)
                // 2. Transforma el Mono<Categoria> al flujo de productos (Flux<Producto>)
                .flatMapMany(categoria -> {
                    // Si la categoría existe, busca todos los productos que tienen ese categoriaId
                    Long categoriaId = categoria.getId();
                    return productoRepository.findByCategoriaId(categoriaId)
                            // 3. Para cada Producto encontrado, lo combina con la Categoria (que ya tenemos)
                            // y lo mapea al DTO de respuesta.
                            .map(producto -> toRespuestaDto(producto, categoria));
                })
                // 4. Si la categoría no se encuentra (el Mono<Categoria> estaba vacío),
                // emitimos un Mono vacío, resultando en un Flux vacío (sin productos).
                .switchIfEmpty(Flux.empty())
                // 5. En caso de error (e.g., Categoría no existe), se podría manejar con Mono.error,
                // pero para una búsqueda, devolver un Flux.empty() es a menudo más amigable.
                // Si el flatMapMany se ejecuta, el Mono<Categoria> ya no está vacío.
                .onErrorResume(NoSuchElementException.class, e -> {
                    // Log o manejo de errores específicos si es necesario, devolviendo Flux vacío.
                    return Flux.empty();
                });
    }

    /**
     * 💰 Obtiene todos los productos que pertenecen a una categoría específica
     * y cuyo precio unitario coincide exactamente con el valor dado.
     *
     * @param nombreCategoria El nombre de la categoría a buscar.
     * @param precioUnitario El precio exacto del producto a buscar.
     * @return Flux que emite DTOs de Respuesta para los productos encontrados.
     */
    public Flux<ProductoRespuestaDTO> obtenerProductosPorCategoriaYPrecio(String nombreCategoria, double precioUnitario) {
        // 1. Busca la entidad Categoria por su nombre
        return categoriaRepository.findByNombreContainingIgnoreCase(nombreCategoria)
                // 2. flatMapMany: Permite encadenar la búsqueda de Categoria con la búsqueda de Productos
                .flatMapMany(categoria -> {
                    // La DB realiza el filtrado combinado para máxima eficiencia
                    Long categoriaId = categoria.getId();

                    // 3. Acceso directo a la base de datos con predicado compuesto (CategoriaId AND PrecioUnitario)
                    return productoRepository.findByCategoriaIdAndPrecioUnitario(categoriaId, precioUnitario)
                            // 4. Mapea el resultado (Producto) al DTO de respuesta, inyectando la Categoria
                            .map(producto -> toRespuestaDto(producto, categoria));
                })
                // 5. Si no hay categoría o no hay productos que coincidan, devuelve un flujo vacío.
                .switchIfEmpty(Flux.empty());
    }
}
