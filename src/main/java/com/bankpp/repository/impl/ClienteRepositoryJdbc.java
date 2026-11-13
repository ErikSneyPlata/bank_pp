package com.bankpp.repository.impl;

import com.bankpp.config.DatabaseConfig;
import com.bankpp.model.Cliente;
import com.bankpp.model.DatosContacto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ClienteRepositoryJdbc {
    public List<Cliente> listar(String filtro) {
        String sql = "SELECT u.id,u.documento,u.nombres,u.apellidos,u.correo,u.activo,d.telefono,d.direccion " +
                "FROM usuarios u LEFT JOIN datos_contacto d ON d.usuario_id=u.id " +
                "WHERE u.rol='CLIENTE' AND (?='' OR u.documento ILIKE ? OR u.nombres ILIKE ? " +
                "OR u.apellidos ILIKE ? OR u.correo ILIKE ?) ORDER BY u.apellidos,u.nombres";
        String patron = filtro == null || filtro.isBlank() ? "" : "%" + filtro.trim() + "%";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, patron);
            for (int i = 2; i <= 5; i++) p.setString(i, patron);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) clientes.add(new Cliente(r.getObject("id", UUID.class), r.getString("documento"),
                        r.getString("nombres"), r.getString("apellidos"), r.getString("correo"),
                        r.getBoolean("activo"), new DatosContacto(r.getString("telefono"), r.getString("direccion"))));
            }
            return clientes;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar los clientes", e);
        }
    }

    public void actualizar(Cliente cliente) {
        String usuarioSql = "UPDATE usuarios SET nombres=?,apellidos=?,correo=? WHERE id=? AND rol='CLIENTE'";
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement p = c.prepareStatement(usuarioSql)) {
                p.setString(1, cliente.getNombres()); p.setString(2, cliente.getApellidos());
                p.setString(3, cliente.getCorreo()); p.setObject(4, cliente.getId());
                if (p.executeUpdate() == 0) throw new IllegalArgumentException("Cliente no encontrado");
                try (PreparedStatement d = c.prepareStatement(
                        "INSERT INTO datos_contacto(usuario_id,telefono,direccion) VALUES(?,?,?) " +
                        "ON CONFLICT(usuario_id) DO UPDATE SET telefono=EXCLUDED.telefono,direccion=EXCLUDED.direccion")) {
                    d.setObject(1, cliente.getId()); d.setString(2, cliente.getDatosContacto().getTelefono());
                    d.setString(3, cliente.getDatosContacto().getDireccion()); d.executeUpdate();
                }
                c.commit();
            } catch (Exception e) { c.rollback(); throw e; }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) throw new IllegalArgumentException("Ya existe un usuario con ese correo", e);
            throw new IllegalStateException("No se pudo actualizar el cliente", e);
        } catch (RuntimeException e) { throw e; }
        catch (Exception e) { throw new IllegalStateException("No se pudo actualizar el cliente", e); }
    }

    public void desactivar(UUID clienteId) {
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(
                "UPDATE usuarios SET activo=FALSE WHERE id=? AND rol='CLIENTE' AND activo=TRUE")) {
            p.setObject(1, clienteId);
            if (p.executeUpdate() == 0) throw new IllegalArgumentException("Cliente no encontrado o ya está inactivo");
        } catch (SQLException e) { throw new IllegalStateException("No se pudo desactivar el cliente", e); }
    }
}
