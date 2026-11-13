package com.bankpp.model;

import com.bankpp.model.enums.EstadoCita;
import java.time.Instant;
import java.util.UUID;

public final class Cita {
    private final UUID id, clienteId;
    private final Instant fechaHora;
    private final String motivo;
    private EstadoCita estado;

    public Cita(UUID id, UUID clienteId, Instant fechaHora, String motivo, EstadoCita estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }
}
