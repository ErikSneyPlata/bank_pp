package com.bankpp.repository.impl;

import com.bankpp.config.DatabaseConfig;
import com.bankpp.model.*;
import com.bankpp.model.enums.RolUsuario;
import java.sql.*;
import java.util.UUID;

public final class AuthRepositoryJdbc {
    public Usuario autenticar(String correo, String contrasena) {
        String sql = "SELECT u.id,u.documento,u.nombres,u.apellidos,u.correo,u.rol,u.activo,d.telefono,d.direccion FROM usuarios u LEFT JOIN datos_contacto d ON d.usuario_id=u.id WHERE lower(u.correo)=lower(?) AND u.contrasena=?";
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, correo);
            p.setString(2, contrasena);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next() || !r.getBoolean("activo"))
                    return null;
                return map(r);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al iniciar sesión", e);
        }
    }

    public Usuario registrarCliente(String documento, String nombres, String apellidos, String correo,
            String contrasena, DatosContacto contacto) {
        String sql = "INSERT INTO usuarios(documento,nombres,apellidos,correo,contrasena,rol) VALUES(?,?,?,?,?,'CLIENTE') RETURNING id";
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement p = c.prepareStatement(sql)) {
                p.setString(1, documento);
                p.setString(2, nombres);
                p.setString(3, apellidos);
                p.setString(4, correo);
                p.setString(5, contrasena);
                UUID id;
                try (ResultSet r = p.executeQuery()) {
                    r.next();
                    id = r.getObject(1, UUID.class);
                }
                try (PreparedStatement d = c
                        .prepareStatement("INSERT INTO datos_contacto(usuario_id,telefono,direccion) VALUES(?,?,?)")) {
                    d.setObject(1, id);
                    d.setString(2, contacto.getTelefono());
                    d.setString(3, contacto.getDireccion());
                    d.executeUpdate();
                }
                try (PreparedStatement s = c.prepareStatement(
                        "INSERT INTO solicitudes_cuenta(cliente_id) VALUES (?)")) {
                    s.setObject(1, id);
                    s.executeUpdate();
                }
                c.commit();
                return new Cliente(id, documento, nombres, apellidos, correo, true, contacto);
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                String constraint = e.getMessage() == null ? "" : e.getMessage();
                if (constraint.contains("documento")) {
                    throw new IllegalArgumentException("Ya existe un usuario con ese documento.", e);
                }
                if (constraint.contains("correo")) {
                    throw new IllegalArgumentException("Ya existe un usuario con ese correo.", e);
                }
            }
            throw new IllegalStateException("No se pudo registrar el cliente y su solicitud de cuenta.", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo registrar el cliente y su solicitud de cuenta.", e);
        }
    }

    private Usuario map(ResultSet r) throws SQLException {
        UUID id = r.getObject("id", UUID.class);
        return RolUsuario.valueOf(r.getString("rol")) == RolUsuario.CLIENTE
                ? new Cliente(id, r.getString("documento"), r.getString("nombres"), r.getString("apellidos"),
                        r.getString("correo"), r.getBoolean("activo"),
                        new DatosContacto(r.getString("telefono"), r.getString("direccion")))
                : new Empleado(id, r.getString("documento"), r.getString("nombres"), r.getString("apellidos"),
                        r.getString("correo"), r.getBoolean("activo"));
    }
}
