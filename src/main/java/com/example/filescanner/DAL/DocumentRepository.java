package com.example.filescanner.DAL;

import com.example.filescanner.BEE.Document;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentRepository {

    // -----------------------------
    // CREATE DOCUMENT
    // -----------------------------
    public int createDocument(int boxId, String barcode) throws SQLException, IOException {

        String sql = "INSERT INTO Documents (BoxId, Barcode) " +
                "OUTPUT INSERTED.DocumentId VALUES (?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, boxId);

            // Sikrer at barcode ALDRIG er null
            if (barcode == null || barcode.isEmpty()) {
                barcode = "NO_BARCODE";
            }

            stmt.setString(2, barcode);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return -1;
    }

    // -----------------------------
    // GET DOCUMENTS BY BOX ID
    // -----------------------------
    public List<Document> getDocumentsByBoxId(int boxId) throws SQLException, IOException {
        List<Document> docs = new ArrayList<>();

        String sql = "SELECT DocumentId, BoxId, Barcode FROM Documents WHERE BoxId = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, boxId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("DocumentId");
                String barcode = rs.getString("Barcode");

                docs.add(new Document(id, boxId, barcode));
            }
        }

        return docs;
    }
}
