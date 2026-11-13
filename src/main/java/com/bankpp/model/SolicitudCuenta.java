package com.bankpp.model;

import com.bankpp.model.enums.EstadoSolicitudCuenta;
import java.time.Instant;
import java.util.UUID;

public final class SolicitudCuenta {
    private final UUID id;
    private final UUID clienteId;
    private final String documento;
    private final String nombres;
    private final String apellidos;
    private final String correo;
    private final EstadoSolicitudCuenta estado;
    private final Instant creadaEn;

    public SolicitudCuenta(UUID id, UUID clienteId, String documento, String nombres, String apellidos,
            String correo, EstadoSolicitudCuenta estado, Instant creadaEn) {
        this.id = id;
        this.clienteId = clienteId;
        this.documento = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.estado = estado;
        this.creadaEn = creadaEn;
    }

    public UUID getId() { return id; }
    public UUID getClienteId() { return clienteId; }
    public String getDocumento() { return documento; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCorreo() { return correo; }
    public EstadoSolicitudCuenta getEstado() { return estado; }
    public Instant getCreadaEn() { return creadaEn; }
}
