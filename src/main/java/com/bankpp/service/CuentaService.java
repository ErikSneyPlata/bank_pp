package com.bankpp.service;

import com.bankpp.model.CuentaBancaria;
import com.bankpp.repository.impl.CuentaRepositoryJdbc;
import java.math.BigDecimal;
import java.util.*;

public final class CuentaService {
    private final CuentaRepositoryJdbc repositorio;

    public CuentaService(CuentaRepositoryJdbc repositorio) {
        this.repositorio = repositorio;
    }

    public List<CuentaBancaria> listarCuentas(UUID usuario) {
        return repositorio.listarPorUsuario(usuario);
    }

    public BigDecimal consultarSaldo(UUID cuenta, UUID usuario) {
        return repositorio.consultarSaldo(cuenta, usuario)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta inexistente o sin permiso"));
    }

    public Optional<CuentaBancaria> buscarPorNumero(String numero) {
        return repositorio.buscarPorNumero(numero);
    }

    public List<CuentaBancaria> listarTodas(com.bankpp.model.Usuario empleado) {
        exigirEmpleado(empleado); return repositorio.listarTodas();
    }

    public void bloquear(java.util.UUID cuentaId, com.bankpp.model.Usuario empleado) {
        exigirEmpleado(empleado); repositorio.cambiarEstado(cuentaId, com.bankpp.model.enums.EstadoCuenta.BLOQUEADA);
    }

    public void cerrar(java.util.UUID cuentaId, com.bankpp.model.Usuario empleado) {
        exigirEmpleado(empleado); repositorio.cambiarEstado(cuentaId, com.bankpp.model.enums.EstadoCuenta.CERRADA);
    }

    private void exigirEmpleado(com.bankpp.model.Usuario usuario) {
        if (usuario == null || !usuario.isActivo() || usuario.getRol() != com.bankpp.model.enums.RolUsuario.EMPLEADO)
            throw new SecurityException("Solo un empleado activo puede gestionar cuentas");
    }}
