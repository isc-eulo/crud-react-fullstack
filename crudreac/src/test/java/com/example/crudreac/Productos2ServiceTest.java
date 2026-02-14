package com.example.crudreac;


import com.example.crudreac.dominio.entity.Categoria;
import com.example.crudreac.dominio.entity.Producto;
import com.example.crudreac.dominio.repository.CategoriasRepository;
import com.example.crudreac.dominio.repository.ProductosRepository;
import com.example.crudreac.dto.ProductoCreacionDTO;
import com.example.crudreac.dto.ProductoRespuestaDTO;
import com.example.crudreac.service.Productos2Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class Productos2ServiceTest {

    @Mock
    private ProductosRepository productoRepository;

    @Mock
    private CategoriasRepository categoriaRepository;

    @InjectMocks
    private Productos2Service productos2Service;

    // Datos de prueba
    private Producto producto1;
    private Categoria categoria1;
    private ProductoCreacionDTO creacionDTO;

    @BeforeEach
    void setUp() {
        categoria1 = new Categoria(1L, "Electrónica");

        producto1 = new Producto(
                100L,
                "Laptop Gamer",
                categoria1.getId(),
                1200.00,
                5
        );

        creacionDTO = new ProductoCreacionDTO();
        creacionDTO.setNombre("Nuevo Producto");
        creacionDTO.setCategoria(categoria1.getNombre());
        creacionDTO.setPrecioUnitario(50.00);
        creacionDTO.setStock(10);
    }

    // --------------------------------------------------------------------------------
    // 1. crearProducto (POST)
    // --------------------------------------------------------------------------------

    @Test
    void crearProducto_exito() {
        // Simulación: La categoría existe
        when(categoriaRepository.findByNombreContainingIgnoreCase(anyString()))
                .thenReturn(Mono.just(categoria1));

        Producto productoGuardado = new Producto(200L, creacionDTO.getNombre(), categoria1.getId(), creacionDTO.getPrecioUnitario(), creacionDTO.getStock());

        when(productoRepository.save(any(Producto.class)))
                .thenReturn(Mono.just(productoGuardado));

        // Verificación reactiva (CORRECCIÓN: Se usa getCategoriaRequestDto().getNombre())
        StepVerifier.create(productos2Service.crearProducto(creacionDTO))
                .expectNextMatches(dto ->
                        dto.getId() == 200L &&
                                dto.getNombre().equals("Nuevo Producto") &&
                                dto.getCategoriaRequestDto().getNombre().equals(categoria1.getNombre()))
                .verifyComplete();
    }

    @Test
    void crearProducto_categoriaNoExiste_debeLanzarExcepcion() {
        when(categoriaRepository.findByNombreContainingIgnoreCase(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(productos2Service.crearProducto(creacionDTO))
                .verifyError(NoSuchElementException.class);
    }

    // --------------------------------------------------------------------------------
    // 2. obtenerProductoPorId (GET /id)
    // --------------------------------------------------------------------------------

    @Test
    void obtenerProductoPorId_exito() {
        when(productoRepository.findById(100L)).thenReturn(Mono.just(producto1));
        when(categoriaRepository.findById(categoria1.getId())).thenReturn(Mono.just(categoria1));

        // Verificación reactiva (CORRECCIÓN: Se usa getCategoriaRequestDto().getNombre())
        StepVerifier.create(productos2Service.obtenerProductoPorId(100L))
                .expectNextMatches(dto ->
                        dto.getId() == 100L &&
                                dto.getCategoriaRequestDto().getId() == categoria1.getId())
                .verifyComplete();
    }

    @Test
    void obtenerProductoPorId_productoNoExiste_debeLanzarExcepcion() {
        when(productoRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(productos2Service.obtenerProductoPorId(999L))
                .verifyError(NoSuchElementException.class);
    }

    // --------------------------------------------------------------------------------
    // 3. obtenerTodosLosProductos (GET /)
    // --------------------------------------------------------------------------------

    @Test
    void obtenerTodosLosProductos_exito() {
        Producto producto2 = new Producto(101L, "Mouse Óptico", categoria1.getId(), 25.00, 20);
        List<Producto> productos = Arrays.asList(producto1, producto2);

        when(productoRepository.findAll()).thenReturn(Flux.fromIterable(productos));
        when(categoriaRepository.findById(categoria1.getId())).thenReturn(Mono.just(categoria1));

        StepVerifier.create(productos2Service.obtenerTodosLosProductos())
                .expectNextCount(2)
                .verifyComplete();
    }

    // --------------------------------------------------------------------------------
    // 4. actualizarProducto (PUT)
    // --------------------------------------------------------------------------------

    @Test
    void actualizarProducto_exito() {
        ProductoCreacionDTO updateDTO = new ProductoCreacionDTO();
        updateDTO.setNombre("Laptop Pro Actualizada");
        updateDTO.setCategoria("Electrónica");
        updateDTO.setPrecioUnitario(1300.00);
        updateDTO.setStock(7);

        Categoria categoriaActualizada = new Categoria(1L, "Electrónica");
        Producto productoActualizado = new Producto(100L, "Laptop Pro Actualizada", 1L, 1300.00, 7);

        // 1. Simula que el producto a actualizar existe
        when(productoRepository.findById(100L)).thenReturn(Mono.just(producto1));
        // 2. Simula que la nueva categoría existe
        when(categoriaRepository.findByNombreContainingIgnoreCase("Electrónica")).thenReturn(Mono.just(categoriaActualizada));
        // 3. Simula el guardado
        when(productoRepository.save(any(Producto.class))).thenReturn(Mono.just(productoActualizado));

        // Verificación reactiva (CORRECCIÓN: Se usa getCategoriaRequestDto().getNombre())
        StepVerifier.create(productos2Service.actualizarProducto(100L, updateDTO))
                .expectNextMatches(dto ->
                        dto.getNombre().equals("Laptop Pro Actualizada") &&
                                dto.getCategoriaRequestDto().getNombre().equals("Electrónica"))
                .verifyComplete();
    }

    @Test
    void actualizarProducto_productoNoExiste_debeLanzarExcepcion() {
        when(productoRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(productos2Service.actualizarProducto(999L, creacionDTO))
                .verifyError(NoSuchElementException.class);
    }

    // --------------------------------------------------------------------------------
    // 5. eliminarProducto (DELETE)
    // --------------------------------------------------------------------------------


    @Test
    void eliminarProducto_productoNoExiste_debeLanzarExcepcion() {
        when(productoRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(productos2Service.eliminarProducto(999L))
                .verifyError(NoSuchElementException.class);
    }

    // --------------------------------------------------------------------------------
    // 6. obtenerProductosPorNombreCategoria (Búsqueda 1)
    // --------------------------------------------------------------------------------

    @Test
    void buscarPorNombreCategoria_exito() {
        Producto producto2 = new Producto(102L, "Auriculares", categoria1.getId(), 50.00, 30);
        List<Producto> productos = Arrays.asList(producto1, producto2);

        when(categoriaRepository.findByNombreContainingIgnoreCase("Electrónica"))
                .thenReturn(Mono.just(categoria1));
        when(productoRepository.findByCategoriaId(categoria1.getId()))
                .thenReturn(Flux.fromIterable(productos));

        StepVerifier.create(productos2Service.obtenerProductosPorNombreCategoria("Electrónica"))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void buscarPorNombreCategoria_categoriaNoExiste_debeRetornarVacio() {
        when(categoriaRepository.findByNombreContainingIgnoreCase(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(productos2Service.obtenerProductosPorNombreCategoria("Inexistente"))
                .expectNextCount(0)
                .verifyComplete();
    }

    // --------------------------------------------------------------------------------
    // 7. obtenerProductosPorCategoriaYPrecio (Búsqueda 2)
    // --------------------------------------------------------------------------------

    @Test
    void buscarPorCategoriaYPrecio_exito() {
        Producto productoCoincidente = new Producto(103L, "Cable HDMI", categoria1.getId(), 15.00, 100);

        when(categoriaRepository.findByNombreContainingIgnoreCase("Electrónica"))
                .thenReturn(Mono.just(categoria1));
        when(productoRepository.findByCategoriaIdAndPrecioUnitario(categoria1.getId(), 15.00))
                .thenReturn(Flux.just(productoCoincidente));

        StepVerifier.create(productos2Service.obtenerProductosPorCategoriaYPrecio("Electrónica", 15.00))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void buscarPorCategoriaYPrecio_productoNoCoincide_debeRetornarVacio() {
        when(categoriaRepository.findByNombreContainingIgnoreCase("Electrónica"))
                .thenReturn(Mono.just(categoria1));
        when(productoRepository.findByCategoriaIdAndPrecioUnitario(categoria1.getId(), 999.99))
                .thenReturn(Flux.empty());

        StepVerifier.create(productos2Service.obtenerProductosPorCategoriaYPrecio("Electrónica", 999.99))
                .expectNextCount(0)
                .verifyComplete();
    }
}
