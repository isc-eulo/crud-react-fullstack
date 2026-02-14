package com.example.crudreac.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO utilizado para devolver la información de un producto al cliente.
 * Incluye el ID generado por la base de datos (PanacheEntity).
 */
@Data
@EqualsAndHashCode(callSuper = true) // Necesario por Lombok al usar herencia
@Schema(description = "DTO utilizado para devolver la información completa de un producto.")
public class ProductoRespuestaDTO extends ProductoBaseDTO {

    /**
     * Identificador único (Clave Primaria) del producto.
     * Corresponde al 'id' generado por PanacheEntity.
     */
    @Schema(example = "12345", readOnly = true)
    private Long id;

    @Schema(name = "categoria")
    private CategoriaRequestDto categoriaRequestDto;
}
