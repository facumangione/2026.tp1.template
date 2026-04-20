package com.bibliotech.model;

public class SocioEstudiante extends Socio {

    public SocioEstudiante(int id, String nombre, String dni, String email) {
        super(id, nombre, dni, email, CategoriaSocio.ESTUDIANTE);
    }

    @Override
    public int getLimitePrestamos() {
        return 3;
    }
}