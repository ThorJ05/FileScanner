package com.example.filescanner.DAL;

import com.example.filescanner.BEE.BasicUser;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements IUserRepository {

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE UserName = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new BasicUser(
                        rs.getString("UserId"),
                        rs.getString("UserName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("PasswordHash"),
                        UserRole.valueOf(rs.getString("Role"))
                );
                return Optional.of(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // ⭐ NEW: Check if email already exists
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Email = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void createUser(User user) {
        String sql = "INSERT INTO Users (UserId, UserName, PasswordHash, Role, FirstName, LastName, Email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getId());
            stmt.setString(2, user.getFirstName());   // UserName
            stmt.setString(3, user.getPassword());    // PasswordHash
            stmt.setString(4, user.getRole().toString());
            stmt.setString(5, user.getFirstName());
            stmt.setString(6, user.getLastName());
            stmt.setString(7, user.getEmail());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(String userId) {
        String sql = "DELETE FROM Users WHERE UserId = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new BasicUser(
                        rs.getString("UserId"),
                        rs.getString("UserName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("PasswordHash"),
                        UserRole.valueOf(rs.getString("Role"))
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
