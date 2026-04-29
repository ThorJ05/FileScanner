package com.example.filescanner.Util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Use this when verifying login
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}