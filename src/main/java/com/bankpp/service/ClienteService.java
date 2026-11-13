package com.bankpp.service;

import com.bankpp.model.Cliente;
import com.bankpp.model.Usuario;
import com.bankpp.model.enums.RolUsuario;
import com.bankpp.repository.impl.ClienteRepositoryJdbc;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ClienteService {
    private final ClienteRepositoryJdbc repositorio;
    public ClienteService(ClienteRepositoryJdbc repositorio) { this.repositorio = Objects.requireNonNull(repositorio); }

    public List<Cliente> listar(Usuario empleado, String filtro) {
        exigirEmpleado(empleado); return repositorio.listar(filtro);
    }
    public void actualizar(Usuario empleado, Cliente cliente) {
        exigirEmpleado(empleado); Objects.requireNonNull(cliente);
        if (vacio(cliente.getNombres()) || vacio(cliente.getApellidos()) || vacio(cliente.getCorreo()))
            throw new IllegalArgumentException("Nombres, apellidos y correo son obligatorios");
        repositorio.actualizar(cliente);
    }
    public void desactivar(Usuario empleado, UUID clienteId) {
        exigirEmpleado(empleado); repositorio.desactivar(Objects.requireNonNull(clienteId));
    }
    private void exigirEmpleado(Usuario usuario) {
        if (usuario == null || !usuario.isActivo() || usuario.getRol() != RolUsuario.EMPLEADO)
            throw new SecurityException("Solo un empleado activo puede gestionar clientes");
    }
    private boolean vacio(String valor) { return valor == null || valor.isBlank(); }
}
