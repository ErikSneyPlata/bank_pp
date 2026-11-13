package com.bankpp.repository.impl;

import com.bankpp.config.DatabaseConfig;
import com.bankpp.model.*;
import com.bankpp.model.enums.TipoTransaccion;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class TransaccionRepositoryJdbc {
    public void retirar(Transaccion t) {
        if (t.getTipo() != TipoTransaccion.RETIRO || !t.validar())
            throw new IllegalArgumentException("Retiro inválido");
        ejecutar(t, false);
    }

    public void transferir(Transaccion t) {
        if (t.getTipo() != TipoTransaccion.TRANSFERENCIA || !t.validar())
            throw new IllegalArgumentException("Transferencia inválida");
        ejecutar(t, true);
    }

    private void ejecutar(Transaccion t, boolean transferencia) {
        String sql = "SELECT usuario_id,saldo,estado FROM cuentas WHERE id=? FOR UPDATE";
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try {
                CuentaRow origen = bloquear(c, sql, t.getCuentaOrigenId());
                if (origen == null)
                    throw new IllegalArgumentException("Cuenta origen inexistente");
                if (!origen.usuario.equals(t.getActorId()))
                    throw new SecurityException("La cuenta no pertenece al usuario");
                if (!"ACTIVA".equals(origen.estado))
                    throw new IllegalStateException("Cuenta origen inactiva");
                if (origen.saldo.compareTo(t.getMonto()) < 0)
                    throw new IllegalArgumentException("Saldo insuficiente");
                if (transferencia) {
                    CuentaRow destino = bloquear(c, sql, t.getCuentaDestinoId());
                    if (destino == null)
                        throw new IllegalArgumentException("Cuenta destino inexistente");
                    if (!"ACTIVA".equals(destino.estado))
                        throw new IllegalStateException("Cuenta destino inactiva");
                }
                try (PreparedStatement p = c.prepareStatement("UPDATE cuentas SET saldo=saldo-? WHERE id=?")) {
                    p.setBigDecimal(1, t.getMonto());
                    p.setObject(2, t.getCuentaOrigenId());
                    p.executeUpdate();
                }
                if (transferencia)
                    try (PreparedStatement p = c.prepareStatement("UPDATE cuentas SET saldo=saldo+? WHERE id=?")) {
                        p.setBigDecimal(1, t.getMonto());
                        p.setObject(2, t.getCuentaDestinoId());
                        p.executeUpdate();
                    }
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO transacciones(cuenta_origen_id,cuenta_destino_id,usuario_actor_id,tipo,monto,descripcion) VALUES(?,?,?,?,?,?)")) {
                    p.setObject(1, t.getCuentaOrigenId());
                    p.setObject(2, t.getCuentaDestinoId());
                    p.setObject(3, t.getActorId());
                    p.setString(4, t.getTipo().name());
                    p.setBigDecimal(5, t.getMonto());
                    p.setString(6, t.getDescripcion());
                    p.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("La operación se revirtió por un error de base de datos", e);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private CuentaRow bloquear(Connection c, String sql, UUID id) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setObject(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? new CuentaRow(r.getObject(1, UUID.class), r.getBigDecimal(2), r.getString(3)) : null;
            }
        }
    }

    private record CuentaRow(UUID usuario, BigDecimal saldo, String estado) {
    }

    public List<Transaccion> listarMovimientos(UUID cuenta, UUID usuario) {
        String sql = "SELECT t.id,t.cuenta_origen_id,t.cuenta_destino_id,t.usuario_actor_id,t.tipo,t.monto,t.fecha,t.descripcion FROM transacciones t WHERE (t.cuenta_origen_id=? OR t.cuenta_destino_id=?) AND EXISTS(SELECT 1 FROM cuentas c WHERE c.id=? AND c.usuario_id=?) ORDER BY t.fecha DESC";
        List<Transaccion> lista = new ArrayList<>();
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setObject(1, cuenta);
            p.setObject(2, cuenta);
            p.setObject(3, cuenta);
            p.setObject(4, usuario);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    UUID id = r.getObject("id", UUID.class), origen = r.getObject("cuenta_origen_id", UUID.class),
                            destino = r.getObject("cuenta_destino_id", UUID.class),
                            actor = r.getObject("usuario_actor_id", UUID.class);
                    BigDecimal monto = r.getBigDecimal("monto");
                    Instant fecha = r.getTimestamp("fecha").toInstant();
                    String descripcion = r.getString("descripcion");
                    lista.add(mapear(id, origen, destino, actor, monto, fecha, descripcion, r.getString("tipo")));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar los movimientos", e);
        }
    }

    public List<Transaccion> listarMovimientosEmpleado(String filtroCuenta) {
        String filtro = filtroCuenta == null || filtroCuenta.isBlank() ? "" : "%" + filtroCuenta.trim() + "%";
        String sql = "SELECT t.id,t.cuenta_origen_id,t.cuenta_destino_id,t.usuario_actor_id,t.tipo,t.monto,t.fecha,t.descripcion " +
                "FROM transacciones t WHERE (?='' OR EXISTS(SELECT 1 FROM cuentas c WHERE c.id=t.cuenta_origen_id AND c.numero ILIKE ?) " +
                "OR EXISTS(SELECT 1 FROM cuentas c WHERE c.id=t.cuenta_destino_id AND c.numero ILIKE ?)) ORDER BY t.fecha DESC";
        List<Transaccion> lista = new ArrayList<>();
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, filtro); p.setString(2, filtro); p.setString(3, filtro);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    UUID id=r.getObject("id",UUID.class), origen=r.getObject("cuenta_origen_id",UUID.class),
                            destino=r.getObject("cuenta_destino_id",UUID.class), actor=r.getObject("usuario_actor_id",UUID.class);
                    BigDecimal monto=r.getBigDecimal("monto"); Instant fecha=r.getTimestamp("fecha").toInstant();
                    String descripcion=r.getString("descripcion");
                    lista.add(mapear(id, origen, destino, actor, monto, fecha, descripcion, r.getString("tipo")));
                }
            }
            return lista;
        } catch (SQLException e) { throw new IllegalStateException("No se pudieron consultar las transacciones", e); }
    }
    public void depositar(Transaccion deposito) {
        if (deposito.getTipo() != TipoTransaccion.DEPOSITO || !deposito.validar())
            throw new IllegalArgumentException("Depósito inválido");
        String sql="SELECT saldo,estado FROM cuentas WHERE id=? FOR UPDATE";
        try(Connection c=DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement p=c.prepareStatement(sql)) {
                    p.setObject(1,deposito.getCuentaOrigenId());
                    try(ResultSet r=p.executeQuery()) {
                        if(!r.next()) throw new IllegalArgumentException("Cuenta inexistente");
                        if(!"ACTIVA".equals(r.getString("estado"))) throw new IllegalStateException("La cuenta no está activa");
                    }
                }
                try(PreparedStatement p=c.prepareStatement("UPDATE cuentas SET saldo=saldo+? WHERE id=? AND estado='ACTIVA'")) {
                    p.setBigDecimal(1,deposito.getMonto());p.setObject(2,deposito.getCuentaOrigenId());
                    if(p.executeUpdate()!=1) throw new IllegalStateException("No se pudo acreditar el depósito");
                }
                insertarTransaccion(c,deposito);
                c.commit();
            } catch(Exception e) { c.rollback(); throw e; }
        } catch(SQLException e) { throw new IllegalStateException("El depósito se revirtió por un error de base de datos",e); }
        catch(RuntimeException e) { throw e; }
    }

    private void insertarTransaccion(Connection c, Transaccion t) throws SQLException {
        try(PreparedStatement p=c.prepareStatement("INSERT INTO transacciones(cuenta_origen_id,cuenta_destino_id,usuario_actor_id,tipo,monto,descripcion) VALUES(?,?,?,?,?,?)")) {
            p.setObject(1,t.getCuentaOrigenId());p.setObject(2,t.getCuentaDestinoId());p.setObject(3,t.getActorId());
            p.setString(4,t.getTipo().name());p.setBigDecimal(5,t.getMonto());p.setString(6,t.getDescripcion());p.executeUpdate();
        }
    }
    private Transaccion mapear(UUID id, UUID origen, UUID destino, UUID actor, BigDecimal monto,
            Instant fecha, String descripcion, String tipo) {
        return switch (TipoTransaccion.valueOf(tipo)) {
            case DEPOSITO -> new Deposito(id, origen, actor, monto, fecha, descripcion);
            case RETIRO -> new Retiro(id, origen, actor, monto, fecha, descripcion);
            case TRANSFERENCIA -> new Transferencia(id, origen, destino, actor, monto, fecha, descripcion);
        };
    }}
