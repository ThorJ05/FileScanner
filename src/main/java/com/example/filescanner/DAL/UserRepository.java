package com.example.filescanner.DAL;

import com.example.filescanner.BEE.BasicUser;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;

import java.io.IOException;
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
                        String.valueOf(rs.getInt("UserId")),
                        rs.getString("UserName"),
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

    @Override
    public void createUser(User user) {
        String sql = "INSERT INTO Users (UserName, PasswordHash, Role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(String userId) {
        String sql = "UPDATE Users SET IsDeleted = 1 WHERE UserId = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void restoreUser(String userId) {
        String sql = "UPDATE Users SET IsDeleted = 0 WHERE UserId = ?";

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
        String sql = "SELECT * FROM Users WHERE IsDeleted = 0";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new BasicUser(
                        String.valueOf(rs.getInt("UserId")),
                        rs.getString("UserName"),
                        rs.getString("PasswordHash"),
                        UserRole.valueOf(rs.getString("Role"))
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }

    public List<User> getDeletedUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE IsDeleted = 1";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new BasicUser(
                        String.valueOf(rs.getInt("UserId")),
                        rs.getString("UserName"),
                        rs.getString("PasswordHash"),
                        UserRole.valueOf(rs.getString("Role"))
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    @Override
    public boolean emailExists(String email) {
        return false;
    }@Override
    public void updatePassword(String userId, String hashedPassword) {

    }
}
