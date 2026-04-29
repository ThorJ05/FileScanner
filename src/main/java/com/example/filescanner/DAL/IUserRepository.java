package com.example.filescanner.DAL;

import com.example.filescanner.BEE.User;
import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    Optional<User> findByUsername(String username);

    void createUser(User user);

    void deleteUser(String userId);

    List<User> getAllUsers();

    // Added for user management operations
    boolean emailExists(String email);

    void updatePassword(String userId, String hashedPassword);
}
