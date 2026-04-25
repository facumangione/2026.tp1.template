package com.bibliotech.repository;

import com.bibliotech.model.Ebook;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EbookRepositoryImpl implements Repository<Ebook, String> {

    private final Map<String, Ebook> storage = new HashMap<>();

    @Override
    public void guardar(Ebook ebook) {
        storage.put(ebook.isbn(), ebook);
    }

    @Override
    public Optional<Ebook> buscarPorId(String isbn) {
        return Optional.ofNullable(storage.get(isbn));
    }

    @Override
    public List<Ebook> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void eliminar(String isbn) {
        storage.remove(isbn);
    }

    public List<Ebook> buscarPorTitulo(String titulo) {
        return storage.values().stream()
                .filter(e -> e.titulo().toLowerCase().contains(titulo.toLowerCase()))
                .toList();
    }

    public List<Ebook> buscarPorAutor(String autor) {
        return storage.values().stream()
                .filter(e -> e.autor().toLowerCase().contains(autor.toLowerCase()))
                .toList();
    }

    public List<Ebook> buscarPorCategoria(String categoria) {
        return storage.values().stream()
                .filter(e -> e.categoria().toLowerCase().contains(categoria.toLowerCase()))
                .toList();
    }
}