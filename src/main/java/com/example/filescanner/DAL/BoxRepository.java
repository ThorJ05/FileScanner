package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Box;

import java.io.IOException;
import java.sql.*;

public class BoxRepository {

    public Box getOrCreateBox(int userId) throws SQLException, IOException {

        String sql = "SELECT TOP 1 BoxId FROM Box WHERE userId = ? ORDER BY BoxId DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Box(rs.getInt("BoxId"), userId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return createNewBox(userId);
    }

    public Box createNewBox(int userId) throws SQLException, IOException {
        String sql = "INSERT INTO Box (ArchiveId, BoxLabel, userId) OUTPUT INSERTED.BoxId VALUES (6, 'New Box', ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Box(rs.getInt(1), userId);
            }
        }

        throw new SQLException("Could not create box");
    }
}
