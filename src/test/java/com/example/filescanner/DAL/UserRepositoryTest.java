package com.example.filescanner.DAL;


import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private final UserRepository repo = new UserRepository();

    @Test
    void login_AdminCredentials_ReturnsAdminUser() {
        User user = repo.login("admin", "admin123");

        assertNotNull(user);
        assertEquals(UserRole.ADMIN, user.getRole());
        assertEquals("Admin", user.getFirstName());
    }

    @Test
    void login_UserCredentials_ReturnsRegularUser() {
        User user = repo.login("user", "user123");

        assertNotNull(user);
        assertEquals(UserRole.USER, user.getRole());
        assertEquals("Regular", user.getFirstName());
    }

    @Test
    void login_InvalidCredentials_ReturnsNull() {
        User user = repo.login("wrong", "wrong");

        assertNull(user);
    }
}

