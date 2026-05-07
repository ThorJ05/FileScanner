package com.example.filescanner.DAL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PageRepository {

    public void createPage(int documentId, int pageNumber, String filePath) throws SQLException, IOException {
        String sql = "INSERT INTO Page (documentId, pageNumber, filePath) VALUES (?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            stmt.setInt(2, pageNumber);
            stmt.setString(3, filePath);

            stmt.executeUpdate();
        }
    }
}
