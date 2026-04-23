package com.bibliotech.repository;

import com.bibliotech.model.Libro;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LibroRepositoryImpl implements Repository<Libro, String> {

    private final Map<String, Libro> storage = new HashMap<>();

    @Override
    public void guardar(Libro libro) {
        storage.put(libro.isbn(), libro);
    }

    @Override
    public Optional<Libro> buscarPorId(String isbn) {
        return Optional.ofNullable(storage.get(isbn));
    }

    @Override
    public List<Libro> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void eliminar(String isbn) {
        storage.remove(isbn);
    }

    // Búsqueda avanzada por título, autor o categoría
    public List<Libro> buscarPorTitulo(String titulo) {
        return storage.values().stream()
                .filter(l -> l.titulo().toLowerCase().contains(titulo.toLowerCase()))
                .toList();
    }

    public List<Libro> buscarPorAutor(String autor) {
        return storage.values().stream()
                .filter(l -> l.autor().toLowerCase().contains(autor.toLowerCase()))
                .toList();
    }

    public List<Libro> buscarPorCategoria(String categoria) {
        return storage.values().stream()
                .filter(l -> l.categoria().toLowerCase().contains(categoria.toLowerCase()))
                .toList();
    }
}