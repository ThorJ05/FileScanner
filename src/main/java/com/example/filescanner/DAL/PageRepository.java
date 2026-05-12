package com.example.filescanner.DAL;

import com.example.filescanner.BEE.ScannedFile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PageRepository {

    // -----------------------------
    // CREATE PAGE
    // -----------------------------
    public void createPage(int documentId, int pageNumber, String filePath, int referenceId, byte[] imageBytes) throws Exception {

        String sql = "INSERT INTO dbo.Page (DocumentId, PageNumber, FilePath, reference_id, Image) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            stmt.setInt(2, pageNumber);
            stmt.setString(3, filePath);
            stmt.setInt(4, referenceId);
            stmt.setBytes(5, imageBytes);

            stmt.executeUpdate();
        }
    }

    // -----------------------------
    // LAZY LOAD PAGES (NO IMAGES)
    // -----------------------------
    public List<ScannedFile> getPagesByDocumentId(int documentId) throws Exception {

        List<ScannedFile> pages = new ArrayList<>();

        String sql = "SELECT PageId, PageNumber, reference_id, FilePath " +
                "FROM dbo.Page WHERE DocumentId = ? ORDER BY reference_id";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                int pageId = rs.getInt("PageId");
                int pageNumber = rs.getInt("PageNumber");
                int referenceId = rs.getInt("reference_id");
                String filePath = rs.getString("FilePath");

                // IMPORTANT: image = null (lazy loading)
                ScannedFile sf = new ScannedFile(
                        "Page " + pageNumber,
                        null,          // image is NOT loaded here
                        null,
                        filePath
                );

                sf.setPageId(pageId);
                sf.setReferenceId(referenceId);

                pages.add(sf);
            }
        }

        return pages;
    }

    // -----------------------------
    // LOAD IMAGE BY PAGE ID (LAZY)
    // -----------------------------
    public byte[] getImageBytes(int pageId) throws Exception {

        String sql = "SELECT Image FROM dbo.Page WHERE PageId = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pageId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBytes("Image");
            }
        }

        return null;
    }

    // -----------------------------
    // UPDATE PAGE NUMBER
    // -----------------------------
    public void updatePageNumber(int documentId, int newPageNumber, String filePath) throws Exception {

        String sql = "UPDATE dbo.Page SET PageNumber = ? WHERE DocumentId = ? AND FilePath = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newPageNumber);
            stmt.setInt(2, documentId);
            stmt.setString(3, filePath);

            stmt.executeUpdate();
        }
    }
}
