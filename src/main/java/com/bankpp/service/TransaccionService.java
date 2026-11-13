package com.bankpp.service;

import com.bankpp.model.*;
import com.bankpp.repository.impl.TransaccionRepositoryJdbc;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public final class TransaccionService {
    private final TransaccionRepositoryJdbc repositorio;

    public TransaccionService(TransaccionRepositoryJdbc repositorio) {
        this.repositorio = repositorio;
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0 || monto.scale() > 2)
            throw new IllegalArgumentException("Monto positivo con máximo dos decimales requerido");
    }

    public void retirar(UUID cuenta, UUID usuario, BigDecimal monto) {
        validarMonto(monto);
        Retiro r = new Retiro(null, cuenta, usuario, monto, Instant.now(), "Retiro");
        if (!r.validar())
            throw new IllegalArgumentException("Retiro inválido");
        repositorio.retirar(r);
    }

    public void transferir(UUID origen, UUID destino, UUID usuario, BigDecimal monto) {
        validarMonto(monto);
        Transferencia t = new Transferencia(null, origen, destino, usuario, monto, Instant.now(), "Transferencia");
        if (!t.validar())
            throw new IllegalArgumentException("Transferencia inválida");
        repositorio.transferir(t);
    }

    public List<Transaccion> consultarMovimientos(UUID cuenta, UUID usuario) {
        return repositorio.listarMovimientos(cuenta, usuario);
    }

    public List<Transaccion> consultarTodas(com.bankpp.model.Usuario empleado, String filtroCuenta) {
        if (empleado == null || !empleado.isActivo() || empleado.getRol() != com.bankpp.model.enums.RolUsuario.EMPLEADO)
            throw new SecurityException("Solo un empleado activo puede consultar todas las transacciones");
        return repositorio.listarMovimientosEmpleado(filtroCuenta);
    }
    public void depositar(java.util.UUID cuentaId, com.bankpp.model.Usuario empleado, BigDecimal monto) {
        validarMonto(monto);
        if (empleado == null || !empleado.isActivo() || empleado.getRol() != com.bankpp.model.enums.RolUsuario.EMPLEADO)
            throw new SecurityException("Solo un empleado activo puede registrar depósitos");
        Deposito deposito = new Deposito(null, cuentaId, empleado.getId(), monto, Instant.now(), "Depósito en ventanilla");
        if (!deposito.validar()) throw new IllegalArgumentException("Depósito inválido");
        repositorio.depositar(deposito);
    }}
