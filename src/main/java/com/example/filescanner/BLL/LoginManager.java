package com.example.filescanner.BLL;

import com.example.filescanner.BEE.User;
import com.example.filescanner.DAL.UserRepository;

import java.util.Optional;

public class LoginManager {

    private final UserRepository userRepository = new UserRepository();

    public Optional<User> attemptLogin(String username, String password) {
        Optional<User> found = userRepository.findByUsername(username);

        if (found.isEmpty()) {
            System.out.println("DEBUG: No user found with username: " + username);
            return Optional.empty();
        }

        User u = found.get();
        System.out.println("DEBUG: Found user: " + u.getFirstName());
        System.out.println("DEBUG: Stored hash: " + u.getPassword());
        System.out.println("DEBUG: Password entered: " + password);
        boolean match = u.checkPassword(password);
        System.out.println("DEBUG: Password match: " + match);

        return match ? Optional.of(u) : Optional.empty();
    }
}