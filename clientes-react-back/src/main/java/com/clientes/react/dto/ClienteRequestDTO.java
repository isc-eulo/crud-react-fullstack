package com.clientes.react.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ClienteRequestDTO extends ClienteBaseDTO {
// Aquí irían campos que solo existen al recibir datos

}
