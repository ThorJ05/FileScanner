package com.example.filescanner.BLL;

import com.example.filescanner.BEE.User;
import com.example.filescanner.DAL.UserRepository;
import com.example.filescanner.Util.PasswordUtil;

import java.util.Optional;

public class LoginManager {

    private final com.example.filescanner.DAL.IUserRepository userRepository;

    public LoginManager() {
        this.userRepository = new com.example.filescanner.DAL.UserRepository();
    }


    public Optional<User> attemptLogin(String username, String password) {
        Optional<User> found = userRepository.findByUsername(username);

        if (found.isEmpty()) {
            System.out.println("DEBUG: No user found with username: " + username);
            return Optional.empty();
        }

        User u = found.get();
        boolean match = u.checkPassword(password);

        return match ? Optional.of(u) : Optional.empty();
    }
}