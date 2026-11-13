package com.bankpp.model;

import java.util.*;

public final class Banco {
    private final UUID id;
    private final String nombre;
    private final List<Empleado> empleados = new ArrayList<>();

    public Banco(UUID id, String nombre) {
        this.id = Objects.requireNonNull(id);
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("Nombre requerido");
        this.nombre = nombre.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void agregarEmpleado(Empleado empleado) {
        empleados.add(Objects.requireNonNull(empleado));
    }

    public void retirarEmpleado(Empleado empleado) {
        empleados.remove(empleado);
    }

    public List<Empleado> getEmpleados() {
        return Collections.unmodifiableList(empleados);
    }
}
