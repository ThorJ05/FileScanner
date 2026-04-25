package com.example.filescanner.BLL;

import com.example.filescanner.BEE.User;
import com.example.filescanner.DAL.UserRepository;

import java.util.Optional;

public class LoginManager {

    private final UserRepository userRepository = new UserRepository();

    public Optional<User> attemptLogin(String username, String password) {
        return userRepository.login(username, password);
    }
}
