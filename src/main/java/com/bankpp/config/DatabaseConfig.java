package com.bankpp.config;

import java.sql.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class DatabaseConfig {
    private static final Map<String, String> DOTENV = loadDotenv();

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        String url = env("BANKPP_JDBC_URL", "jdbc:postgresql://localhost:5432/bankpp");
        String user = env("BANKPP_DB_USER", "postgres"), password = env("BANKPP_DB_PASSWORD", null);
        if (password == null)
            throw new SQLException("Configure BANKPP_DB_PASSWORD en el entorno o en el archivo local .env.");
        return DriverManager.getConnection(url, user, password);
    }

    private static String env(String k, String d) {
        String v = System.getenv(k);
        if (v == null || v.isBlank()) {
            v = DOTENV.get(k);
        }
        return v == null || v.isBlank() ? d : v;
    }

    private static Map<String, String> loadDotenv() {
        Map<String, String> values = new HashMap<>();
        Path path = Path.of(".env");
        if (!Files.isRegularFile(path)) {
            return values;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                String entry = line.trim();
                if (entry.isEmpty() || entry.startsWith("#")) {
                    continue;
                }

                int separator = entry.indexOf('=');
                if (separator <= 0) {
                    continue;
                }

                String key = entry.substring(0, separator).trim();
                String value = entry.substring(separator + 1).trim();
                if (value.length() >= 2 &&
                        ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                values.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo local .env.", e);
        }
        return values;
    }
}
