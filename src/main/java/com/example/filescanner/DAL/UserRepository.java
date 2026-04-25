package com.example.filescanner.DAL;

import com.example.filescanner.BEE.BasicUser;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        users.add(new BasicUser("1", "admin", "User", "admin@test.com", "admin123", UserRole.ADMIN));
        users.add(new BasicUser("2", "user", "User", "user@test.com", "user123", UserRole.USER));
    }

    @Override
    public User login(String username, String password) {
        return users.stream()
                .filter(u -> u.getFirstName().equalsIgnoreCase(username))
                .filter(u -> u.checkPassword(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createUser(User user) {
        users.add(user);
    }

    @Override
    public void deleteUser(String userId) {
        users.removeIf(u -> u.getId().equals(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}