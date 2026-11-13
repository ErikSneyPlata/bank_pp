package com.bankpp.model;

import com.bankpp.model.enums.RolUsuario;
import java.util.UUID;

public final class Empleado extends Usuario {
    public Empleado(UUID id, String documento, String nombres, String apellidos, String correo, boolean activo) {
        super(id, documento, nombres, apellidos, correo, RolUsuario.EMPLEADO, activo);
    }
}
