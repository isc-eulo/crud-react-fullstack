package com.clientes.react.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder // Permite usar el Builder con herencia
@NoArgsConstructor
@AllArgsConstructor
public abstract class ClienteBaseDTO {
    //@NotBlank(message = "El nombre es requerido")
    private String nombre;

  //  @NotBlank(message = "La identificación es requerida")
    private String identificacion;

 //   @Email(message = "Formato de email inválido")
    private String email;
}
