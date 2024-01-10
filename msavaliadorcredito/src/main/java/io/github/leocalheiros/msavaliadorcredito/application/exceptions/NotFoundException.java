package io.github.leocalheiros.msavaliadorcredito.application.exceptions;

public class NotFoundException extends Exception{
    public NotFoundException() {
        super("ClientNotFound");
    }
}
