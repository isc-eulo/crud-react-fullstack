package com.clientes.react.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO extends ClienteBaseDTO {
    private Long id;
}