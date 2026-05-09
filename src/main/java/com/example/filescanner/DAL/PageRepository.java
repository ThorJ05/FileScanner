package com.example.filescanner.DAL;

import com.example.filescanner.BEE.ScannedFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PageRepository {

    // -----------------------------
    // CREATE PAGE
    // -----------------------------
    public void createPage(int documentId, int pageNumber, String filePath) throws SQLException, IOException {
        String sql = "INSERT INTO Page (DocumentId, PageNumber, FilePath) VALUES (?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            stmt.setInt(2, pageNumber);
            stmt.setString(3, filePath);

            stmt.executeUpdate();
        }
    }


    public List<ScannedFile> getPagesByDocumentId(int documentId) throws SQLException, IOException {
        List<ScannedFile> pages = new ArrayList<>();

        String sql = "SELECT PageNumber, FilePath FROM Page WHERE DocumentId = ? ORDER BY PageNumber";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int pageNumber = rs.getInt("PageNumber");
                String filePath = rs.getString("FilePath");

                // LOad pictures
                BufferedImage img = javax.imageio.ImageIO.read(new java.io.File(filePath));

                pages.add(new ScannedFile("Page " + pageNumber, img, null, filePath));
            }
        }

        return pages;
    }

    public void updatePageNumber(int documentId, int pageNumber, String filePath) throws SQLException, IOException {
        String sql = "UPDATE Page SET PageNumber = ? WHERE DocumentId = ? AND FilePath = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pageNumber);
            stmt.setInt(2, documentId);
            stmt.setString(3, filePath);

            stmt.executeUpdate();
        }
    }

}
