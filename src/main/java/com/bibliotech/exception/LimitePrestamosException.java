package com.bibliotech.exception;

public class LimitePrestamosException extends BibliotecaException {
    public LimitePrestamosException(String nombreSocio) {
        super("El socio " + nombreSocio + " alcanzó su límite de préstamos.");
    }
}