package com.example.filescanner.BLL;

import com.example.filescanner.BEE.BasicUser;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;
import com.example.filescanner.DAL.UserRepository;
import com.example.filescanner.Util.PasswordUtil;

import java.util.List;
import java.util.UUID;

public class UserManager {

    private final UserRepository repo = new UserRepository();

    public boolean emailExists(String email) {
        return repo.emailExists(email);
    }

    public void createUser(String username, String first, String last, String email, String password, UserRole role) {
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new BasicUser(
                UUID.randomUUID().toString(),
                username,
                last,
                email,
                hashedPassword,
                role
        );

        repo.createUser(user);
    }

    // ONE-TIME migration — delete this method after running once
    public void rehashAllPlainTextPasswords() {
        List<User> users = repo.getAllUsers();
        for (User u : users) {
            String pw = u.getPassword();
            if (pw != null && !pw.startsWith("$2a$")) {
                String hashed = PasswordUtil.hashPassword(pw);
                repo.updatePassword(u.getId(), hashed);
            }
        }
    }

    public void deleteUser(String id) {
        repo.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return repo.getAllUsers();
    }
}