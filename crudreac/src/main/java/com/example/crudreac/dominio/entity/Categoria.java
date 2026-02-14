package com.example.crudreac.dominio.entity;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("categoria")
public class Categoria {

    @Id
    private Long id; // ID de la clave primaria

    /**
     * Nombre descriptivo de la categoría (ej: Electrónica, Limpieza).
     */
    private String nombre;
}
