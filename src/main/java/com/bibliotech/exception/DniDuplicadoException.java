package com.bibliotech.exception;

public class DniDuplicadoException extends BibliotecaException {
    public DniDuplicadoException(String dni) {
        super("Ya existe un socio registrado con el DNI: " + dni);
    }
}