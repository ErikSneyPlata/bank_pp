package com.bankpp.model;

import com.bankpp.model.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public abstract class Transaccion {
    private final UUID id, cuentaOrigenId, cuentaDestinoId, actorId;
    private final BigDecimal monto;
    private final Instant fecha;
    private final String descripcion;

    protected Transaccion(UUID id, UUID origen, UUID destino, UUID actor, BigDecimal monto, Instant fecha,
            String descripcion) {
        if (monto == null || monto.signum() <= 0 || monto.scale() > 2)
            throw new IllegalArgumentException("Monto positivo con máximo dos decimales requerido");
        this.id = id;
        this.cuentaOrigenId = origen;
        this.cuentaDestinoId = destino;
        this.actorId = actor;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public abstract TipoTransaccion getTipo();

    public abstract boolean validar();

    public UUID getId() {
        return id;
    }

    public UUID getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public UUID getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public UUID getActorId() {
        return actorId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public Instant getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
