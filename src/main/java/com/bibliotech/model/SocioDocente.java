package com.bibliotech.model;

public class SocioDocente extends Socio {

    public SocioDocente(int id, String nombre, String dni, String email) {
        super(id, nombre, dni, email, CategoriaSocio.DOCENTE);
    }

    @Override
    public int getLimitePrestamos() {
        return 5;
    }
}