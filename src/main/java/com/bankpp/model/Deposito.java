package com.bankpp.model;

import com.bankpp.model.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class Deposito extends Transaccion {
    public Deposito(UUID id, UUID cuenta, UUID actor, BigDecimal monto, Instant fecha, String descripcion) {
        super(id, cuenta, null, actor, monto, fecha, descripcion);
        if (cuenta == null || actor == null) throw new IllegalArgumentException("Cuenta y empleado requeridos");
    }
    @Override public TipoTransaccion getTipo() { return TipoTransaccion.DEPOSITO; }
    @Override public boolean validar() { return getMonto().signum() > 0 && getCuentaOrigenId() != null; }
}
