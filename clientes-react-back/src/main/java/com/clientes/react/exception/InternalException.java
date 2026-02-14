package com.clientes.react.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InternalException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    public InternalException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}