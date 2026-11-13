package com.bankpp.model;

public final class DatosContacto {
    private final String telefono, direccion;

    public DatosContacto(String telefono, String direccion) {
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }
}
