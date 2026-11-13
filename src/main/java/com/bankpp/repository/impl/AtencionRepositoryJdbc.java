package com.bankpp.repository.impl;

import com.bankpp.config.DatabaseConfig;
import com.bankpp.model.*;
import com.bankpp.model.enums.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public final class AtencionRepositoryJdbc {
    public void crearCita(UUID cliente, Instant fecha, String motivo) {
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement p = c
                        .prepareStatement("INSERT INTO citas(cliente_id,fecha_hora,motivo) VALUES(?,?,?)")) {
            p.setObject(1, cliente);
            p.setTimestamp(2, Timestamp.from(fecha));
            p.setString(3, motivo);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo registrar la cita", e);
        }
    }

    public List<Cita> listarCitas(UUID cliente) {
        List<Cita> citas = new ArrayList<>();
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement p = c.prepareStatement(
                        "SELECT id,cliente_id,fecha_hora,motivo,estado FROM citas WHERE cliente_id=? ORDER BY fecha_hora DESC")) {
            p.setObject(1, cliente);
            try (ResultSet r = p.executeQuery()) {
                while (r.next())
                    citas.add(new Cita(r.getObject(1, UUID.class), r.getObject(2, UUID.class),
                            r.getTimestamp(3).toInstant(), r.getString(4), EstadoCita.valueOf(r.getString(5))));
            }
            return citas;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar las citas", e);
        }
    }

    public void cancelarCita(UUID id, UUID cliente) {
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement p = c.prepareStatement(
                        "UPDATE citas SET estado='CANCELADA' WHERE id=? AND cliente_id=? AND estado IN ('SOLICITADA','CONFIRMADA')")) {
            p.setObject(1, id);
            p.setObject(2, cliente);
            if (p.executeUpdate() == 0)
                throw new IllegalArgumentException("Cita inexistente o no cancelable");
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo cancelar la cita", e);
        }
    }

    public void crearSolicitud(UUID cliente, String descripcion) {
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement p = c
                        .prepareStatement("INSERT INTO solicitudes_atencion(cliente_id,descripcion) VALUES(?,?)")) {
            p.setObject(1, cliente);
            p.setString(2, descripcion);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo crear la solicitud", e);
        }
    }

    public List<SolicitudAtencion> listarSolicitudes(UUID cliente) {
        List<SolicitudAtencion> solicitudes = new ArrayList<>();
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement p = c.prepareStatement(
                        "SELECT id,cliente_id,descripcion,estado FROM solicitudes_atencion WHERE cliente_id=? ORDER BY creada_en DESC")) {
            p.setObject(1, cliente);
            try (ResultSet r = p.executeQuery()) {
                while (r.next())
                    solicitudes.add(new SolicitudAtencion(r.getObject(1, UUID.class), r.getObject(2, UUID.class),
                            r.getString(3), EstadoSolicitud.valueOf(r.getString(4))));
            }
            return solicitudes;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar las solicitudes", e);
        }
    }

    public List<Cita> listarTodasCitas() {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT id,cliente_id,fecha_hora,motivo,estado FROM citas ORDER BY fecha_hora";
        try (Connection c=DatabaseConfig.getConnection(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()) {
            while(r.next()) citas.add(new Cita(r.getObject("id",UUID.class),r.getObject("cliente_id",UUID.class),
                    r.getTimestamp("fecha_hora").toInstant(),r.getString("motivo"),EstadoCita.valueOf(r.getString("estado"))));
            return citas;
        } catch(SQLException e) { throw new IllegalStateException("No se pudieron consultar las citas",e); }
    }

    public void cambiarEstadoCita(UUID citaId, UUID empleadoId, EstadoCita estado) {
        String sql="UPDATE citas SET estado=?,empleado_asignado_id=? WHERE id=? AND estado IN ('SOLICITADA','CONFIRMADA')";
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,estado.name());p.setObject(2,empleadoId);p.setObject(3,citaId);
            if(p.executeUpdate()==0) throw new IllegalArgumentException("Cita no encontrada o ya finalizada");
        } catch(SQLException e) { throw new IllegalStateException("No se pudo actualizar la cita",e); }
    }

    public List<SolicitudAtencion> listarTodasSolicitudes() {
        List<SolicitudAtencion> solicitudes=new ArrayList<>();
        String sql="SELECT id,cliente_id,descripcion,estado FROM solicitudes_atencion ORDER BY creada_en DESC";
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()) {
            while(r.next()) solicitudes.add(new SolicitudAtencion(r.getObject("id",UUID.class),r.getObject("cliente_id",UUID.class),
                    r.getString("descripcion"),EstadoSolicitud.valueOf(r.getString("estado"))));
            return solicitudes;
        } catch(SQLException e) { throw new IllegalStateException("No se pudieron consultar las solicitudes de atención",e); }
    }

    public void cambiarEstadoSolicitud(UUID solicitudId, UUID empleadoId, EstadoSolicitud estado) {
        String sql="UPDATE solicitudes_atencion SET estado=?,empleado_asignado_id=?,actualizado_en=CURRENT_TIMESTAMP " +
                "WHERE id=? AND estado IN ('ABIERTA','EN_PROCESO')";
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,estado.name());p.setObject(2,empleadoId);p.setObject(3,solicitudId);
            if(p.executeUpdate()==0) throw new IllegalArgumentException("Solicitud no encontrada o ya cerrada/resuelta");
        } catch(SQLException e) { throw new IllegalStateException("No se pudo actualizar la solicitud de atención",e); }
    }}
