package com.example.filescanner.DAL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoxRepository {

    public int createBox(int userId) throws SQLException, IOException {

        String sql = "INSERT INTO Box (ArchiveId, BoxLabel, userId) " +
                "OUTPUT INSERTED.BoxId VALUES (?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, 6);               // <-- ArchiveId = 1 (eksisterer nu)
            stmt.setString(2, "New Box");    // BoxLabel
            stmt.setInt(3, userId);          // userId FK

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return -1;
    }

}
