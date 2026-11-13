package com.bankpp.model;

import com.bankpp.model.enums.RolUsuario;
import java.util.Objects;
import java.util.UUID;

public final class Cliente extends Usuario {
    private final DatosContacto datosContacto;

    public Cliente(UUID id, String documento, String nombres, String apellidos, String correo, boolean activo,
            DatosContacto contacto) {
        super(id, documento, nombres, apellidos, correo, RolUsuario.CLIENTE, activo);
        datosContacto = Objects.requireNonNull(contacto, "Los datos de contacto componen el cliente");
    }

    public DatosContacto getDatosContacto() {
        return datosContacto;
    }
}
