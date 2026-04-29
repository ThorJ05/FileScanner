package com.example.filescanner.BLL;

import com.example.filescanner.BEE.BasicUser;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;
import com.example.filescanner.DAL.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserManager {

    private final UserRepository repo = new UserRepository();

    // ⭐ NEW: Check if email exists
    public boolean emailExists(String email) {
        return repo.emailExists(email);
    }

    public void createUser(String username, String first, String last, String email, String password, UserRole role) {

        User user = new BasicUser(
                UUID.randomUUID().toString(),
                username,
                last,
                email,
                password,
                role
        );

        repo.createUser(user);
    }

    public void deleteUser(String id) {
        repo.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return repo.getAllUsers();
    }
}
