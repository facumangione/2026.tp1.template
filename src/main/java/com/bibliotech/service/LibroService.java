package com.bibliotech.service;

import com.bibliotech.exception.LibroNoEncontradoException;
import com.bibliotech.model.Libro;
import com.bibliotech.repository.LibroRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class LibroService {

    private final LibroRepositoryImpl libroRepo;

    public LibroService(LibroRepositoryImpl libroRepo) {
        this.libroRepo = libroRepo;
    }

    public void registrarLibro(String isbn, String titulo, String autor, int anio, String categoria) {
        Libro libro = new Libro(isbn, titulo, autor, anio, categoria, true);
        libroRepo.guardar(libro);
        System.out.println("Libro registrado correctamente: " + titulo);
    }

    public Libro buscarPorIsbn(String isbn) throws LibroNoEncontradoException {
        return libroRepo.buscarPorId(isbn)
                .orElseThrow(() -> new LibroNoEncontradoException(isbn));
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepo.buscarPorTitulo(titulo);
    }

    public List<Libro> buscarPorAutor(String autor) {
        return libroRepo.buscarPorAutor(autor);
    }

    public List<Libro> buscarPorCategoria(String categoria) {
        return libroRepo.buscarPorCategoria(categoria);
    }

    public List<Libro> listarTodos() {
        return libroRepo.buscarTodos();
    }
}