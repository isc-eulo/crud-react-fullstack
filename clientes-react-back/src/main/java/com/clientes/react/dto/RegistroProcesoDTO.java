package com.clientes.react.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class RegistroProcesoDTO extends ClienteBaseDTO{
    private String estado;         // "EXITOSO", "ERROR_NEGOCIO", "ERROR_SISTEMA"
    private String mensaje;        // Descripción del error o "OK"
    private LocalDateTime fecha;
}
