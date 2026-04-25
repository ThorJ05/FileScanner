package com.example.filescanner.DAL;

import com.example.filescanner.BEE.User;
import java.util.List;

public interface IUserRepository {
    User login(String username, String password);
    void createUser(User user);
    void deleteUser(String userId);
    List<User> getAllUsers();
}