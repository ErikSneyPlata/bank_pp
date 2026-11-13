package com.bankpp.repository.impl;

import com.bankpp.config.DatabaseConfig;
import com.bankpp.model.SolicitudCuenta;
import com.bankpp.model.enums.EstadoSolicitudCuenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SolicitudCuentaRepositoryJdbc {
    public void crear(UUID clienteId) {
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try {
                bloquearCliente(c, clienteId);
                try (PreparedStatement cuenta = c.prepareStatement("SELECT 1 FROM cuentas WHERE usuario_id=?")) {
                    cuenta.setObject(1, clienteId);
                    try (ResultSet r = cuenta.executeQuery()) {
                        if (r.next()) throw new IllegalArgumentException("El cliente ya tiene una cuenta abierta.");
                    }
                }
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO solicitudes_cuenta(cliente_id) VALUES (?)")) {
                    p.setObject(1, clienteId);
                    p.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalArgumentException("Ya existe una solicitud de cuenta pendiente para este cliente.", e);
            }
            throw new IllegalStateException("No se pudo enviar la solicitud de apertura de cuenta.", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo enviar la solicitud de apertura de cuenta.", e);
        }
    }

    public Optional<SolicitudCuenta> obtenerUltima(UUID clienteId) {
        String sql = "SELECT s.id,s.cliente_id,u.documento,u.nombres,u.apellidos,u.correo,s.estado,s.creada_en "
                + "FROM solicitudes_cuenta s JOIN usuarios u ON u.id=s.cliente_id "
                + "WHERE s.cliente_id=? ORDER BY s.creada_en DESC LIMIT 1";
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setObject(1, clienteId);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(map(r)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el estado de la solicitud de cuenta.", e);
        }
    }

    public List<SolicitudCuenta> listarPendientes(UUID empleadoId) {
        String sql = "SELECT s.id,s.cliente_id,u.documento,u.nombres,u.apellidos,u.correo,s.estado,s.creada_en "
                + "FROM solicitudes_cuenta s JOIN usuarios u ON u.id=s.cliente_id "
                + "WHERE s.estado='PENDIENTE' ORDER BY s.creada_en";
        try (Connection c = DatabaseConfig.getConnection()) {
            verificarRol(c, empleadoId, "EMPLEADO");
            try (PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
                List<SolicitudCuenta> solicitudes = new ArrayList<>();
                while (r.next()) solicitudes.add(map(r));
                return solicitudes;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar las solicitudes pendientes.", e);
        }
    }

    public String aprobar(UUID solicitudId, UUID empleadoId) {
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try {
                verificarRol(c, empleadoId, "EMPLEADO");
                UUID clienteId;
                try (PreparedStatement p = c.prepareStatement(
                        "SELECT cliente_id FROM solicitudes_cuenta WHERE id=? AND estado='PENDIENTE'")) {
                    p.setObject(1, solicitudId);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new IllegalArgumentException("La solicitud ya fue atendida o no existe.");
                        clienteId = r.getObject(1, UUID.class);
                    }
                }
                bloquearCliente(c, clienteId);
                try (PreparedStatement p = c.prepareStatement(
                        "SELECT 1 FROM solicitudes_cuenta WHERE id=? AND cliente_id=? AND estado='PENDIENTE' FOR UPDATE")) {
                    p.setObject(1, solicitudId);
                    p.setObject(2, clienteId);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new IllegalArgumentException("La solicitud ya fue atendida o no existe.");
                    }
                }

                try (PreparedStatement p = c.prepareStatement("SELECT 1 FROM cuentas WHERE usuario_id=?")) {
                    p.setObject(1, clienteId);
                    try (ResultSet r = p.executeQuery()) {
                        if (r.next()) throw new IllegalArgumentException("El cliente ya tiene una cuenta abierta.");
                    }
                }

                UUID cuentaId;
                String numeroCuenta;
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO cuentas(usuario_id,numero) VALUES (?, nextval('cuenta_numero_seq')::text) RETURNING id,numero")) {
                    p.setObject(1, clienteId);
                    try (ResultSet r = p.executeQuery()) {
                        r.next();
                        cuentaId = r.getObject(1, UUID.class);
                        numeroCuenta = r.getString(2);
                    }
                }

                try (PreparedStatement p = c.prepareStatement(
                        "UPDATE solicitudes_cuenta SET estado='APROBADA',resuelta_en=CURRENT_TIMESTAMP,empleado_id=?,cuenta_id=? WHERE id=?")) {
                    p.setObject(1, empleadoId);
                    p.setObject(2, cuentaId);
                    p.setObject(3, solicitudId);
                    p.executeUpdate();
                }
                c.commit();
                return numeroCuenta;
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo aprobar la solicitud de cuenta.", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo aprobar la solicitud de cuenta.", e);
        }
    }

    public void rechazar(UUID solicitudId, UUID empleadoId) {
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try {
                verificarRol(c, empleadoId, "EMPLEADO");
                UUID clienteId;
                try (PreparedStatement p = c.prepareStatement(
                        "SELECT cliente_id FROM solicitudes_cuenta WHERE id=? AND estado='PENDIENTE'")) {
                    p.setObject(1, solicitudId);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new IllegalArgumentException("La solicitud ya fue atendida o no existe.");
                        clienteId = r.getObject(1, UUID.class);
                    }
                }
                bloquearCliente(c, clienteId);
                try (PreparedStatement p = c.prepareStatement(
                        "UPDATE solicitudes_cuenta SET estado='RECHAZADA',resuelta_en=CURRENT_TIMESTAMP,empleado_id=? WHERE id=? AND cliente_id=? AND estado='PENDIENTE'")) {
                    p.setObject(1, empleadoId);
                    p.setObject(2, solicitudId);
                    p.setObject(3, clienteId);
                    if (p.executeUpdate() == 0) throw new IllegalArgumentException("La solicitud ya fue atendida o no existe.");
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo rechazar la solicitud de cuenta.", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo rechazar la solicitud de cuenta.", e);
        }
    }

    private void verificarRol(Connection c, UUID usuarioId, String rol) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "SELECT 1 FROM usuarios WHERE id=? AND rol=? AND activo=TRUE")) {
            p.setObject(1, usuarioId);
            p.setString(2, rol);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new IllegalArgumentException("El usuario no tiene permiso para esta acción.");
            }
        }
    }

    private void bloquearCliente(Connection c, UUID clienteId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "SELECT 1 FROM usuarios WHERE id=? AND rol='CLIENTE' AND activo=TRUE FOR UPDATE")) {
            p.setObject(1, clienteId);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new IllegalArgumentException("No se encontró un cliente activo para esta solicitud.");
            }
        }
    }

    private SolicitudCuenta map(ResultSet r) throws SQLException {
        return new SolicitudCuenta(r.getObject("id", UUID.class), r.getObject("cliente_id", UUID.class),
                r.getString("documento"), r.getString("nombres"), r.getString("apellidos"),
                r.getString("correo"), EstadoSolicitudCuenta.valueOf(r.getString("estado")),
                r.getTimestamp("creada_en").toInstant());
    }
}
