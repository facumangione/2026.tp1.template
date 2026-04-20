package com.bibliotech.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Socio {

    private final int id;
    private final String nombre;
    private final String dni;
    private final String email;
    private final CategoriaSocio categoria;
    private final List<String> librosPrestados;
    private boolean bloqueado;

    public Socio(int id, String nombre, String dni, String email, CategoriaSocio categoria) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.categoria = categoria;
        this.librosPrestados = new ArrayList<>();
        this.bloqueado = false;
    }

    // Cada subclase define su límite
    public abstract int getLimitePrestamos();

    // Lógica común: no bloqueado Y tiene cupo disponible
    public boolean puedeTomarPrestado() {
        return !bloqueado && librosPrestados.size() < getLimitePrestamos();
    }

    public void agregarPrestamo(String isbn) {
        librosPrestados.add(isbn);
    }

    public void devolverPrestamo(String isbn) {
        librosPrestados.remove(isbn);
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDni() { return dni; }
    public String getEmail() { return email; }
    public CategoriaSocio getCategoria() { return categoria; }
    public List<String> getLibrosPrestados() { return librosPrestados; }
    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }
}