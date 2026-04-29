package com.example.filescanner.DAL;

import com.example.filescanner.BEE.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreCorrect() {
        UserRepository repo = new UserRepository();

        Optional<User> result = repo.login("admin", "admin123");

        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getFirstName());
    }

    @Test
    void login_ShouldFail_WhenPasswordIsWrong() {
        UserRepository repo = new UserRepository();

        Optional<User> result = repo.login("admin", "wrongpassword");

        assertTrue(result.isEmpty());
    }
}
