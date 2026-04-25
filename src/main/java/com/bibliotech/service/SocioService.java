package com.bibliotech.service;

import com.bibliotech.exception.DniDuplicadoException;
import com.bibliotech.exception.SocioNoEncontradoException;
import com.bibliotech.model.Socio;
import com.bibliotech.model.SocioDocente;
import com.bibliotech.model.SocioEstudiante;
import com.bibliotech.repository.SocioRepositoryImpl;

import java.util.List;

public class SocioService {

    private final SocioRepositoryImpl socioRepo;
    private int contadorId = 1;

    public SocioService(SocioRepositoryImpl socioRepo) {
        this.socioRepo = socioRepo;
    }

    public void registrarEstudiante(String nombre, String dni, String email) throws DniDuplicadoException {
        validarDniUnico(dni);
        validarEmailFormato(email);
        Socio socio = new SocioEstudiante(contadorId++, nombre, dni, email);
        socioRepo.guardar(socio);
        System.out.println("Estudiante registrado correctamente: " + nombre);
    }

    public void registrarDocente(String nombre, String dni, String email) throws DniDuplicadoException {
        validarDniUnico(dni);
        validarEmailFormato(email);
        Socio socio = new SocioDocente(contadorId++, nombre, dni, email);
        socioRepo.guardar(socio);
        System.out.println("Docente registrado correctamente: " + nombre);
    }

    public Socio buscarPorId(int id) throws SocioNoEncontradoException {
        return socioRepo.buscarPorId(id)
                .orElseThrow(() -> new SocioNoEncontradoException(id));
    }

    public List<Socio> listarTodos() {
        return socioRepo.buscarTodos();
    }

    private void validarDniUnico(String dni) throws DniDuplicadoException {
        if (socioRepo.buscarPorDni(dni).isPresent()) {
            throw new DniDuplicadoException(dni);
        }
    }

    private void validarEmailFormato(String email) {
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new RuntimeException("El email no tiene un formato válido: " + email);
        }
    }
}