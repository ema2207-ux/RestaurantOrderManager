package com.restaurant.gui;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("-------------------------------------------");
        System.out.println("   RESTO MANAGER PRO v2.0 - STARTING UP   ");
        System.out.println("   Developed by: Radulescu Emanuel-Andrei ");
        System.out.println("-------------------------------------------");

        try {
            // Actualizăm schema bazei de date pentru noile cerințe de raportare (Manager)
            updateDatabaseSchema();

            // Check Java version
            String version = System.getProperty("java.version");
            System.out.println("[INFO] Java Runtime Version: " + version);
            System.out.println("[INFO] Loading UI Components...");

            RestaurantGui.main(args);
        } catch (Exception e) {
            System.err.println("[CRITICAL ERROR] Failed to launch application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateDatabaseSchema() {
        try (java.sql.Connection conn = com.restaurant.config.DatabaseConfig.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            System.out.println("-------------------------------------------");
            System.out.println("[DB-SYNC] Incepe migrare baza de date...");

            // Incercam cu IF NOT EXISTS (metoda sigura)
            String[] updates = {
                "ALTER TABLE bills ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20) DEFAULT 'Numerar'",
                "ALTER TABLE bills ADD COLUMN IF NOT EXISTS employee_name VARCHAR(100) DEFAULT 'Manager'"
            };

            for (String sql : updates) {
                try {
                    stmt.execute(sql);
                    System.out.println("[DB-SYNC] ✓ Executat: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                } catch (java.sql.SQLException e) {
                    String msg = e.getMessage();
                if (msg != null && (msg.contains("already exists") || msg.contains("duplicate") ||
                        msg.contains("column already") || msg.contains("ERROR 42701"))) {
                        System.out.println("[DB-SYNC] ℹ Coloana exista deja");
                    } else {
                        System.out.println("[DB-SYNC] ⚠ Nota (necritic): " + (msg != null ? msg : "Unknown error"));
                    }
                }
            }

            // Verificare coloane finale
            System.out.println("[DB-SYNC] Verific prezenta coloane...");
            try (java.sql.ResultSet rs = stmt.executeQuery(
                "SELECT column_name FROM information_schema.columns WHERE table_name='bills' ORDER BY ordinal_position")) {
                System.out.print("[DB-SYNC] Coloane in bills: ");
                boolean first = true;
                while (rs.next()) {
                    if (!first) System.out.print(", ");
                    System.out.print(rs.getString(1));
                    first = false;
                }
                System.out.println();
            } catch (Exception e) {
                System.out.println("[DB-SYNC] ℹ Nu s-a putut verifica coloane (nu e critic): " + e.getMessage());
            }

            System.out.println("[DB-SYNC] Migrare completata! BillRepository va folosi valori default pentru coloane lipsă.");
            System.out.println("-------------------------------------------");

        } catch (Exception e) {
            System.err.println("[DB-SYNC] EROARE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
