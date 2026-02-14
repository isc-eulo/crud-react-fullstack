package com.example.crudreac.dominio.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Representa un producto en el inventario de la aplicación.
 * <p>
 * Extiende PanacheEntity, lo que proporciona automáticamente:
 * <ul>
 * <li>Un campo 'id' de tipo Long, que es la clave primaria autogenerada.</li>
 * <li>Métodos estáticos para consultas sencillas (persist, findById, listAll).</li>
 * </ul>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table
public class Producto  {

    @Id
    private Long id;

    /**
     * Nombre descriptivo y legible del producto.
     */
    private String nombre;

    /**
     * Categoría a la que pertenece el producto.
     * Ejemplo: "Electrónica", "Limpieza".
     */
    private Long categoriaId;

    /**
     * Precio de venta por unidad del producto.
     */
    private double precioUnitario;

    /**
     * Cantidad de unidades disponibles actualmente en el inventario.
     * Es crucial para el control de stock.
     */
    private int stock;
}