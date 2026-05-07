package com.example.filescanner.DAL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoxRepository {

    public int createBox(int userId) throws SQLException, IOException {
        String sql = "INSERT INTO Box (userId) OUTPUT INSERTED.id VALUES (?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }
}
