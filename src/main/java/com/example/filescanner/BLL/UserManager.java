package com.example.filescanner.BLL;

import com.example.filescanner.BEE.BasicUser;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;
import com.example.filescanner.DAL.IUserRepository;
import com.example.filescanner.DAL.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserManager {

    private final IUserRepository userRepository;

    public UserManager() {
        // Swap UserRepository for a DatabaseUserRepository later
        this.userRepository = new UserRepository();
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public void createUser(String firstName, String lastName,
                           String email, String password, UserRole role) {
        String id = UUID.randomUUID().toString();
        User newUser = new BasicUser(id, firstName, lastName, email, password, role);
        userRepository.createUser(newUser);
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId);
    }
}