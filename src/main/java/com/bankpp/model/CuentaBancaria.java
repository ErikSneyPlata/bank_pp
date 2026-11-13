package com.bankpp.model;

import com.bankpp.model.enums.EstadoCuenta;
import java.math.BigDecimal;
import java.util.UUID;

public final class CuentaBancaria {
    private final UUID id, usuarioId;
    private final String numero, moneda;
    private final BigDecimal saldo;
    private final EstadoCuenta estado;

    public CuentaBancaria(UUID id, UUID usuarioId, String numero, BigDecimal saldo, String moneda,
            EstadoCuenta estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        if (numero == null || numero.isBlank() || saldo == null || saldo.signum() < 0)
            throw new IllegalArgumentException("Cuenta inválida");
        this.numero = numero;
        this.saldo = saldo;
        this.moneda = moneda;
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getNumero() {
        return numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getMoneda() {
        return moneda;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return numero + " | $" + saldo + " " + moneda;
    }
}
