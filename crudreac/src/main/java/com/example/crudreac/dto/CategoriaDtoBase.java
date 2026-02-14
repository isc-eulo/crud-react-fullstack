package com.example.crudreac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos comunes de un producto.")
public class CategoriaDtoBase {
         /* Nombre descriptivo del producto.
            */
    @Schema(example = "Smartphone X10", required = true)
    private String nombre;
}
