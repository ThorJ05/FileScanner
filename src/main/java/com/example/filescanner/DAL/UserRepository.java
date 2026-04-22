package com.example.filescanner.DAL;

import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;

public class UserRepository {

    public User login(String username, String password) {
        if (username.equals("admin") && password.equals("admin123")) {
            return new ConcreteUser("1", "Admin", "User", "admin@test.com", password, UserRole.ADMIN);
        }
        if (username.equals("user") && password.equals("user123")) {
            return new ConcreteUser("2", "Regular", "User", "user@test.com", password, UserRole.USER);
        }
        return null;
    }

    // Temporary concrete implementation of the abstract User class
    private static class ConcreteUser extends User {
        public ConcreteUser(String id, String firstName, String lastName,
                            String email, String password, UserRole role) {
            super(id, firstName, lastName, email, password, role);
        }
    }
}