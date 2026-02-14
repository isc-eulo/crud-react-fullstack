package com.example.crudreac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO utilizado para la creación de un nuevo producto.")
@Data
public class CategoriaRequestDto extends CategoriaDtoBase{
/**
 * Identificador único (Clave Primaria) del producto.
 * Corresponde al 'id' generado por PanacheEntity.
 */
@Schema(example = "12345", readOnly = true)
private Long id;
}
