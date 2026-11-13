package com.bankpp.service;

import com.bankpp.model.*;
import com.bankpp.repository.impl.AuthRepositoryJdbc;

public final class AutenticacionService {
    private final AuthRepositoryJdbc repositorio;

    public AutenticacionService(AuthRepositoryJdbc repositorio) {
        this.repositorio = repositorio;
    }

    public Usuario iniciarSesion(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank())
            throw new IllegalArgumentException("Correo y contraseña obligatorios");
        return repositorio.autenticar(correo.trim(), contrasena);
    }

    public Usuario registrarCliente(String doc, String nombres, String apellidos, String correo, String contrasena,
            String telefono, String direccion) {
        if (contrasena == null || contrasena.isBlank())
            throw new IllegalArgumentException("Contraseña obligatoria");
        if (doc == null || doc.isBlank() || nombres == null || nombres.isBlank() || apellidos == null
                || apellidos.isBlank() || correo == null || correo.isBlank())
            throw new IllegalArgumentException("Completa los datos obligatorios");
        return repositorio.registrarCliente(doc, nombres, apellidos, correo, contrasena,
                new DatosContacto(telefono, direccion));
    }
}
