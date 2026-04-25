package com.bibliotech;

import com.bibliotech.exception.BibliotecaException;
import com.bibliotech.repository.LibroRepositoryImpl;
import com.bibliotech.repository.SocioRepositoryImpl;
import com.bibliotech.service.LibroService;
import com.bibliotech.service.PrestamoService;
import com.bibliotech.service.SocioService;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static LibroService libroService;
    private static SocioService socioService;
    private static PrestamoService prestamoService;

    public static void main(String[] args) {
        LibroRepositoryImpl libroRepo = new LibroRepositoryImpl();
        SocioRepositoryImpl socioRepo = new SocioRepositoryImpl();

        libroService = new LibroService(libroRepo);
        socioService = new SocioService(socioRepo);
        prestamoService = new PrestamoService(libroRepo, socioRepo);

        System.out.println("=== Bienvenido a BiblioTech ===");

        boolean ejecutando = true;
        while (ejecutando) {
            mostrarMenu();
            int opcion = leerEntero("Ingresá una opción: ");
            switch (opcion) {
                case 1 -> menuLibros();
                case 2 -> menuSocios();
                case 3 -> menuPrestamos();
                case 4 -> prestamoService.mostrarHistorial();
                case 0 -> {
                    System.out.println("Hasta luego.");
                    ejecutando = false;
                }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== Menú Principal ===");
        System.out.println("1. Gestión de Libros");
        System.out.println("2. Gestión de Socios");
        System.out.println("3. Gestión de Préstamos");
        System.out.println("4. Ver historial");
        System.out.println("0. Salir");
    }

    private static void menuLibros() {
        System.out.println("\n=== Gestión de Libros ===");
        System.out.println("1. Registrar libro");
        System.out.println("2. Buscar por título");
        System.out.println("3. Buscar por autor");
        System.out.println("4. Buscar por categoría");
        System.out.println("5. Listar todos");
        int opcion = leerEntero("Ingresá una opción: ");
        switch (opcion) {
            case 1 -> {
                String isbn = leerTexto("ISBN: ");
                String titulo = leerTexto("Título: ");
                String autor = leerTexto("Autor: ");
                int anio = leerEntero("Año: ");
                String categoria = leerTexto("Categoría: ");
                libroService.registrarLibro(isbn, titulo, autor, anio, categoria);
            }
            case 2 -> libroService.buscarPorTitulo(leerTexto("Título: ")).forEach(System.out::println);
            case 3 -> libroService.buscarPorAutor(leerTexto("Autor: ")).forEach(System.out::println);
            case 4 -> libroService.buscarPorCategoria(leerTexto("Categoría: ")).forEach(System.out::println);
            case 5 -> libroService.listarTodos().forEach(System.out::println);
            default -> System.out.println("Opción no válida.");
        }
    }

    private static void menuSocios() {
        System.out.println("\n=== Gestión de Socios ===");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Registrar docente");
        System.out.println("3. Listar todos");
        int opcion = leerEntero("Ingresá una opción: ");
        switch (opcion) {
            case 1 -> {
                String nombre = leerTexto("Nombre: ");
                String dni = leerTexto("DNI: ");
                String email = leerTexto("Email: ");
                socioService.registrarEstudiante(nombre, dni, email);
            }
            case 2 -> {
                String nombre = leerTexto("Nombre: ");
                String dni = leerTexto("DNI: ");
                String email = leerTexto("Email: ");
                socioService.registrarDocente(nombre, dni, email);
            }
            case 3 -> socioService.listarTodos().forEach(System.out::println);
            default -> System.out.println("Opción no válida.");
        }
    }

    private static void menuPrestamos() {
        System.out.println("\n=== Gestión de Préstamos ===");
        System.out.println("1. Realizar préstamo");
        System.out.println("2. Realizar devolución");
        int opcion = leerEntero("Ingresá una opción: ");
        try {
            switch (opcion) {
                case 1 -> {
                    String isbn = leerTexto("ISBN del libro: ");
                    int socioId = leerEntero("ID del socio: ");
                    prestamoService.realizarPrestamo(isbn, socioId);
                }
                case 2 -> {
                    String isbn = leerTexto("ISBN del libro: ");
                    int socioId = leerEntero("ID del socio: ");
                    prestamoService.realizarDevolucion(isbn, socioId);
                }
                default -> System.out.println("Opción no válida.");
            }
        } catch (BibliotecaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!scanner.hasNextInt()) {
            System.out.print("Ingresá un número válido: ");
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }
}