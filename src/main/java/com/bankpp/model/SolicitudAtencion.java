package com.bankpp.model;

import com.bankpp.model.enums.EstadoSolicitud;
import java.util.UUID;

public final class SolicitudAtencion {
    private final UUID id, clienteId;
    private final String descripcion;
    private EstadoSolicitud estado;

    public SolicitudAtencion(UUID id, UUID clienteId, String descripcion, EstadoSolicitud estado) {
        this.id = id;
        this.clienteId = clienteId;
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("Descripción requerida");
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }
}
