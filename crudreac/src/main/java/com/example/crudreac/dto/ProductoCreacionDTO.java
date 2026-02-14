package com.example.crudreac.dto;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO utilizado para crear un nuevo producto.
 * Hereda todos los campos de ProductoBaseDTO y se usa en peticiones POST.
 * El ID se genera en el backend y no se expone aquí.
 */
@Schema(description = "DTO utilizado para la creación de un nuevo producto.")
public class ProductoCreacionDTO extends ProductoBaseDTO {
    // No necesita campos adicionales; hereda nombre, categoría, precio, stock.
}