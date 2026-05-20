package com.example.filescanner.BLL;

import com.example.filescanner.BEE.BasicUser;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;
import com.example.filescanner.Util.PasswordUtil;


import java.util.List;
import java.util.UUID;

public class UserManager {

    private final com.example.filescanner.DAL.IUserRepository repo;

    public UserManager() {
        this.repo = new com.example.filescanner.DAL.UserRepository();
    }

    public void createUser(String username, String password, UserRole role) {
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new BasicUser(
                UUID.randomUUID().toString(),
                username,
                hashedPassword,
                role
        );

        repo.createUser(user);
    }
    public void restoreUser(String id) {
        repo.restoreUser(id);
    }

    public List<User> getDeletedUsers() {
        return repo.getDeletedUsers();
    }

    public void deleteUser(String id) {
        repo.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return repo.getAllUsers();
    }
}
