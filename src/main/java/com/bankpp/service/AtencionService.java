package com.bankpp.service;

import com.bankpp.model.*;
import com.bankpp.model.enums.EstadoCita;
import com.bankpp.model.enums.EstadoSolicitud;
import com.bankpp.repository.impl.AtencionRepositoryJdbc;
import java.time.Instant;
import java.util.*;

public final class AtencionService {
    private final AtencionRepositoryJdbc repositorio;

    public AtencionService(AtencionRepositoryJdbc repositorio) {
        this.repositorio = repositorio;
    }

    public void solicitarCita(UUID cliente, Instant fecha, String motivo) {
        if (fecha == null || !fecha.isAfter(Instant.now()))
            throw new IllegalArgumentException("La cita debe ser futura");
        if (motivo == null || motivo.isBlank())
            throw new IllegalArgumentException("El motivo es obligatorio");
        repositorio.crearCita(cliente, fecha, motivo.trim());
    }

    public List<Cita> listarCitas(UUID cliente) {
        return repositorio.listarCitas(cliente);
    }

    public void cancelarCita(UUID cita, UUID cliente) {
        repositorio.cancelarCita(cita, cliente);
    }

    public void crearSolicitud(UUID cliente, String descripcion) {
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria");
        repositorio.crearSolicitud(cliente, descripcion.trim());
    }

    public List<SolicitudAtencion> listarSolicitudes(UUID cliente) {
        return repositorio.listarSolicitudes(cliente);
    }

    public List<Cita> listarCitasEmpleado(Usuario empleado) {
        exigirEmpleado(empleado); return repositorio.listarTodasCitas();
    }

    public void gestionarCita(UUID citaId, EstadoCita nuevoEstado, Usuario empleado) {
        exigirEmpleado(empleado);
        if (nuevoEstado != EstadoCita.CONFIRMADA && nuevoEstado != EstadoCita.CANCELADA && nuevoEstado != EstadoCita.COMPLETADA)
            throw new IllegalArgumentException("Estado de gestión no permitido");
        Cita cita = repositorio.listarTodasCitas().stream().filter(c -> c.getId().equals(citaId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        boolean valida = (cita.getEstado() == com.bankpp.model.enums.EstadoCita.SOLICITADA
                    && (nuevoEstado == com.bankpp.model.enums.EstadoCita.CONFIRMADA || nuevoEstado == com.bankpp.model.enums.EstadoCita.CANCELADA))
                || (cita.getEstado() == com.bankpp.model.enums.EstadoCita.CONFIRMADA
                    && (nuevoEstado == com.bankpp.model.enums.EstadoCita.COMPLETADA || nuevoEstado == com.bankpp.model.enums.EstadoCita.CANCELADA));
        if (!valida) throw new IllegalArgumentException("La cita no puede pasar de " + cita.getEstado() + " a " + nuevoEstado);
        repositorio.cambiarEstadoCita(citaId, empleado.getId(), nuevoEstado);
    }

    public List<SolicitudAtencion> listarSolicitudesEmpleado(Usuario empleado) {
        exigirEmpleado(empleado); return repositorio.listarTodasSolicitudes();
    }

    public void gestionarSolicitud(UUID solicitudId, EstadoSolicitud estado, Usuario empleado) {
        exigirEmpleado(empleado);
        if (estado != EstadoSolicitud.EN_PROCESO && estado != EstadoSolicitud.RESUELTA && estado != EstadoSolicitud.CERRADA)
            throw new IllegalArgumentException("Estado de gestión no permitido");
        repositorio.cambiarEstadoSolicitud(solicitudId, empleado.getId(), estado);
    }

    private void exigirEmpleado(Usuario usuario) {
        if (usuario == null || !usuario.isActivo() || usuario.getRol() != com.bankpp.model.enums.RolUsuario.EMPLEADO)
            throw new SecurityException("Solo un empleado activo puede gestionar citas y solicitudes");
    }}
