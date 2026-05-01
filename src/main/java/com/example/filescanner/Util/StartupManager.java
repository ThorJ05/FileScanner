package com.example.filescanner.Util;

import com.example.filescanner.BLL.UserManager;

/**
 * Encapsulates startup-time operations such as migrations.
 * Keeps MainApp focused on application lifecycle.
 */
public final class StartupManager {

    private StartupManager() {}

    public static void runMigrations() {
        try {
            new UserManager().rehashAllPlainTextPasswords();
        } catch (Exception e) {
            System.err.println("Startup migration failed — continuing startup.");
            e.printStackTrace();
        }
    }
}
