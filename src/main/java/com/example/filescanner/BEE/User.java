package com.example.filescanner.BEE;

import org.mindrot.jbcrypt.BCrypt;

public abstract class User {

    private final String id;
    private String username;
    private String password; // hashed password
    private final UserRole role;

    protected User(String id, String username, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password; // already hashed from DB
        this.role = role;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }


    public boolean checkPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);
    }
}
