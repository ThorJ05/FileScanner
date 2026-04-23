package com.example.filescanner.Util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    //for hashing our password
    public static String hashPassword(String password) {

        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
