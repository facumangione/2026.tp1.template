package com.bibliotech.service;

import com.bibliotech.exception.LibroNoEncontradoException;
import com.bibliotech.model.Ebook;
import com.bibliotech.repository.EbookRepositoryImpl;

import java.util.List;

public class EbookService {

    private final EbookRepositoryImpl ebookRepo;

    public EbookService(EbookRepositoryImpl ebookRepo) {
        this.ebookRepo = ebookRepo;
    }

    public void registrarEbook(String isbn, String titulo, String autor, int anio, String categoria, String formato) {
        Ebook ebook = new Ebook(isbn, titulo, autor, anio, categoria, formato);
        ebookRepo.guardar(ebook);
        System.out.println("Ebook registrado correctamente: " + titulo);
    }

    public Ebook buscarPorIsbn(String isbn) throws LibroNoEncontradoException {
        return ebookRepo.buscarPorId(isbn)
                .orElseThrow(() -> new LibroNoEncontradoException(isbn));
    }

    public List<Ebook> buscarPorTitulo(String titulo) {
        return ebookRepo.buscarPorTitulo(titulo);
    }

    public List<Ebook> buscarPorAutor(String autor) {
        return ebookRepo.buscarPorAutor(autor);
    }

    public List<Ebook> buscarPorCategoria(String categoria) {
        return ebookRepo.buscarPorCategoria(categoria);
    }

    public List<Ebook> listarTodos() {
        return ebookRepo.buscarTodos();
    }
}