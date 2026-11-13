package com.bankpp.model;

import com.bankpp.model.enums.RolUsuario;
import java.util.Objects;
import java.util.UUID;

public abstract class Usuario {
    private final UUID id;
    private final String documento, nombres, apellidos, correo;
    private final RolUsuario rol;
    private boolean activo;

    protected Usuario(UUID id, String documento, String nombres, String apellidos, String correo, RolUsuario rol,
            boolean activo) {
        this.id = Objects.requireNonNull(id);
        this.documento = req(documento);
        this.nombres = req(nombres);
        this.apellidos = req(apellidos);
        this.correo = req(correo).toLowerCase();
        this.rol = Objects.requireNonNull(rol);
        this.activo = activo;
    }

    private static String req(String s) {
        if (s == null || s.isBlank())
            throw new IllegalArgumentException("El campo es obligatorio");
        return s.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
