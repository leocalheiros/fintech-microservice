package io.github.leocalheiros.msavaliadorcredito.application.exceptions;


import lombok.Getter;

public class UnknownComunicationException extends Exception{

    @Getter
    private Integer status;

    public UnknownComunicationException(String msg, Integer status) {
        super(msg);
        this.status = status;
    }
}
