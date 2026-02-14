package com.clientes.react.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BdaException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    public BdaException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}