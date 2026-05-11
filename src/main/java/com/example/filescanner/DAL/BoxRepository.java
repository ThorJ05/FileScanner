package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Box;

import java.io.IOException;
import java.sql.*;

public class BoxRepository {

    public Box getOrCreateBox(int userId) throws SQLException, IOException {

        String sql =
                "IF EXISTS (SELECT 1 FROM dbo.Box WHERE userId = ?) " +
                        "BEGIN " +
                        "   SELECT BoxId FROM dbo.Box WHERE userId = ? " +
                        "END " +
                        "ELSE " +
                        "BEGIN " +
                        "   INSERT INTO dbo.Box (ArchiveId, BoxLabel, userId) " +
                        "   OUTPUT INSERTED.BoxId VALUES (?, ?, ?) " +
                        "END";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, 6);
            stmt.setString(4, "New Box");
            stmt.setInt(5, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int boxId = rs.getInt(1);
                System.out.println("Using box: " + boxId);
                return new Box(boxId, userId);
            }
        }

        throw new SQLException("Could not create or retrieve box");
    }
}
