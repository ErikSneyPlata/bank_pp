package com.bankpp.repository.impl;

import com.bankpp.config.DatabaseConfig;
import com.bankpp.model.CuentaBancaria;
import com.bankpp.model.enums.EstadoCuenta;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public final class CuentaRepositoryJdbc {
    public List<CuentaBancaria> listarPorUsuario(UUID usuario) {
        String sql = "SELECT id,usuario_id,numero,saldo,moneda,estado FROM cuentas WHERE usuario_id=? ORDER BY creada_en DESC";
        List<CuentaBancaria> cuentas = new ArrayList<>();
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setObject(1, usuario);
            try (ResultSet r = p.executeQuery()) {
                while (r.next())
                    cuentas.add(map(r));
            }
            return cuentas;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar las cuentas", e);
        }
    }

    public Optional<BigDecimal> consultarSaldo(UUID cuenta, UUID usuario) {
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement p = c.prepareStatement("SELECT saldo FROM cuentas WHERE id=? AND usuario_id=?")) {
            p.setObject(1, cuenta);
            p.setObject(2, usuario);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(r.getBigDecimal(1)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el saldo", e);
        }
    }

    public Optional<CuentaBancaria> buscarPorNumero(String numero) {
        try (Connection c = DatabaseConfig.getConnection();
                PreparedStatement p = c.prepareStatement(
                        "SELECT id,usuario_id,numero,saldo,moneda,estado FROM cuentas WHERE numero=?")) {
            p.setString(1, numero);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(map(r)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se encontró la cuenta destino", e);
        }
    }

    private CuentaBancaria map(ResultSet r) throws SQLException {
        return new CuentaBancaria(r.getObject("id", UUID.class), r.getObject("usuario_id", UUID.class),
                r.getString("numero"), r.getBigDecimal("saldo"), r.getString("moneda"),
                EstadoCuenta.valueOf(r.getString("estado")));
    }

    public List<CuentaBancaria> listarTodas() {
        List<CuentaBancaria> cuentas = new ArrayList<>();
        String sql = "SELECT id,usuario_id,numero,saldo,moneda,estado FROM cuentas ORDER BY creada_en DESC";
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(sql);
                ResultSet r = p.executeQuery()) {
            while (r.next()) cuentas.add(map(r));
            return cuentas;
        } catch (SQLException e) { throw new IllegalStateException("No se pudieron consultar las cuentas", e); }
    }

    public void cambiarEstado(UUID cuentaId, EstadoCuenta estado) {
        try (Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement(
                "UPDATE cuentas SET estado=? WHERE id=? AND estado<>'CERRADA' AND (?<>'CERRADA' OR saldo=0)")) {
            p.setString(1, estado.name()); p.setObject(2, cuentaId); p.setString(3, estado.name());
            if (p.executeUpdate() == 0) throw new IllegalArgumentException("Cuenta no encontrada, cerrada o con saldo distinto de cero");
        } catch (SQLException e) { throw new IllegalStateException("No se pudo cambiar el estado de la cuenta", e); }
    }}
