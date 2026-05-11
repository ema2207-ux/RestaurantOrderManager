package com.restaurant.gui;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("-------------------------------------------");
        System.out.println("   RESTO MANAGER PRO v2.0 - STARTING UP   ");
        System.out.println("   Developed by: Radulescu Emanuel-Andrei ");
        System.out.println("-------------------------------------------");

        try {
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
}
