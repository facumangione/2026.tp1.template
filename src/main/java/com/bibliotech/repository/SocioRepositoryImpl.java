package com.bibliotech.repository;

import com.bibliotech.model.Socio;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SocioRepositoryImpl implements Repository<Socio, Integer> {

    private final Map<Integer, Socio> storage = new HashMap<>();

    @Override
    public void guardar(Socio socio) {
        storage.put(socio.getId(), socio);
    }

    @Override
    public Optional<Socio> buscarPorId(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Socio> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void eliminar(Integer id) {
        storage.remove(id);
    }

    public Optional<Socio> buscarPorDni(String dni) {
        return storage.values().stream()
                .filter(s -> s.getDni().equals(dni))
                .findFirst();
    }

    public Optional<Socio> buscarPorEmail(String email) {
        return storage.values().stream()
                .filter(s -> s.getEmail().equals(email))
                .findFirst();
    }
}