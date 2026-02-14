package com.clientes.react.exception;
import org.springframework.http.HttpStatus;

import lombok.Getter;

public class NegocioException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    public NegocioException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}