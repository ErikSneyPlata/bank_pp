package com.bankpp.model;

import com.bankpp.model.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class Transferencia extends Transaccion {
    public Transferencia(UUID id, UUID origen, UUID destino, UUID actor, BigDecimal monto, Instant fecha,
            String descripcion) {
        super(id, origen, destino, actor, monto, fecha, descripcion);
        if (origen == null || destino == null || origen.equals(destino))
            throw new IllegalArgumentException("Las cuentas deben ser distintas");
    }

    @Override
    public TipoTransaccion getTipo() {
        return TipoTransaccion.TRANSFERENCIA;
    }

    @Override
    public boolean validar() {
        return getMonto().signum() > 0 && getCuentaOrigenId() != null && getCuentaDestinoId() != null
                && !getCuentaOrigenId().equals(getCuentaDestinoId());
    }
}
