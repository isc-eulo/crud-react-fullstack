package com.example.crudreac.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Clase base para la transferencia de datos de Producto.
 * Contiene los campos comunes de negocio (nombre, precio, etc.)
 * para ser heredados por los DTOs de Creación y Respuesta.
 */
@Data
@Schema(description = "Datos comunes de un producto.")
public class ProductoBaseDTO {

    /**
     * Nombre descriptivo del producto.
     */
    @Schema(example = "Smartphone X10", required = true)
    private String nombre;

    /**
     * Categoría a la que pertenece el producto.
     */
    @Schema(example = "Electrónica")
    private String categoria;

    /**
     * Precio de venta por unidad del producto.
     */
    @Schema(example = "499.99", required = true)
    private double precioUnitario;

    /**
     * Cantidad de unidades disponibles actualmente en el inventario.
     */
    @Schema(example = "50")
    private int stock;
}