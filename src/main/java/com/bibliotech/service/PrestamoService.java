package com.bibliotech.service;

import com.bibliotech.exception.*;
import com.bibliotech.model.Libro;
import com.bibliotech.model.Socio;
import com.bibliotech.repository.LibroRepositoryImpl;
import com.bibliotech.repository.SocioRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoService {

    private final LibroRepositoryImpl libroRepo;
    private final SocioRepositoryImpl socioRepo;
    private final List<String> historial = new ArrayList<>();

    public PrestamoService(LibroRepositoryImpl libroRepo, SocioRepositoryImpl socioRepo) {
        this.libroRepo = libroRepo;
        this.socioRepo = socioRepo;
    }

    public void realizarPrestamo(String isbn, int socioId) throws BibliotecaException {
        Libro libro = libroRepo.buscarPorId(isbn)
                .orElseThrow(() -> new LibroNoEncontradoException(isbn));

        if (!libro.disponible()) {
            throw new LibroNoDisponibleException(isbn);
        }

        Socio socio = socioRepo.buscarPorId(socioId)
                .orElseThrow(() -> new SocioNoEncontradoException(socioId));

        if (!socio.puedeTomarPrestado()) {
            throw new LimitePrestamosException(socio.getNombre());
        }

        // Actualizar disponibilidad del libro
        Libro libroActualizado = new Libro(
                libro.isbn(), libro.titulo(), libro.autor(),
                libro.anio(), libro.categoria(), false
        );
        libroRepo.guardar(libroActualizado);

        socio.agregarPrestamo(isbn);
        socioRepo.guardar(socio);

        String registro = "[" + LocalDate.now() + "] PRESTAMO - ISBN: " + isbn + " | Socio ID: " + socioId;
        historial.add(registro);
        System.out.println("Préstamo realizado correctamente.");
    }

    public void realizarDevolucion(String isbn, int socioId) throws BibliotecaException {
        Libro libro = libroRepo.buscarPorId(isbn)
                .orElseThrow(() -> new LibroNoEncontradoException(isbn));

        Socio socio = socioRepo.buscarPorId(socioId)
                .orElseThrow(() -> new SocioNoEncontradoException(socioId));

        Libro libroActualizado = new Libro(
                libro.isbn(), libro.titulo(), libro.autor(),
                libro.anio(), libro.categoria(), true
        );
        libroRepo.guardar(libroActualizado);

        socio.devolverPrestamo(isbn);
        socioRepo.guardar(socio);

        String registro = "[" + LocalDate.now() + "] DEVOLUCION - ISBN: " + isbn + " | Socio ID: " + socioId;
        historial.add(registro);
        System.out.println("Devolución realizada correctamente.");
    }

    public void mostrarHistorial() {
        if (historial.isEmpty()) {
            System.out.println("No hay transacciones registradas.");
            return;
        }
        System.out.println("\n=== Historial de transacciones ===");
        historial.forEach(System.out::println);
    }
}