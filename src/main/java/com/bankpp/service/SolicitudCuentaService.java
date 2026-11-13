package com.bankpp.service;

import com.bankpp.model.SolicitudCuenta;
import com.bankpp.model.Usuario;
import com.bankpp.model.enums.RolUsuario;
import com.bankpp.repository.impl.SolicitudCuentaRepositoryJdbc;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SolicitudCuentaService {
    private final SolicitudCuentaRepositoryJdbc repositorio;

    public SolicitudCuentaService(SolicitudCuentaRepositoryJdbc repositorio) {
        this.repositorio = repositorio;
    }

    public void solicitar(Usuario usuario) {
        exigirRol(usuario, RolUsuario.CLIENTE);
        repositorio.crear(usuario.getId());
    }

    public Optional<SolicitudCuenta> consultarUltima(Usuario usuario) {
        exigirRol(usuario, RolUsuario.CLIENTE);
        return repositorio.obtenerUltima(usuario.getId());
    }

    public List<SolicitudCuenta> listarPendientes(Usuario usuario) {
        exigirRol(usuario, RolUsuario.EMPLEADO);
        return repositorio.listarPendientes(usuario.getId());
    }

    public String aprobar(UUID solicitudId, Usuario usuario) {
        exigirRol(usuario, RolUsuario.EMPLEADO);
        return repositorio.aprobar(solicitudId, usuario.getId());
    }

    public void rechazar(UUID solicitudId, Usuario usuario) {
        exigirRol(usuario, RolUsuario.EMPLEADO);
        repositorio.rechazar(solicitudId, usuario.getId());
    }

    private void exigirRol(Usuario usuario, RolUsuario rol) {
        if (usuario == null || usuario.getRol() != rol || !usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario no tiene permiso para esta acción.");
        }
    }
}
