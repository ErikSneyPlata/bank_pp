package com.bankpp.model;

import com.bankpp.model.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class Retiro extends Transaccion {
    public Retiro(UUID id, UUID origen, UUID actor, BigDecimal monto, Instant fecha, String descripcion) {
        super(id, origen, null, actor, monto, fecha, descripcion);
        if (origen == null)
            throw new IllegalArgumentException("Cuenta origen requerida");
    }

    @Override
    public TipoTransaccion getTipo() {
        return TipoTransaccion.RETIRO;
    }

    @Override
    public boolean validar() {
        return getMonto().signum() > 0 && getCuentaOrigenId() != null;
    }
}
