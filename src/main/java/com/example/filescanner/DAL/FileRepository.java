package com.example.filescanner.DAL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class FileRepository {

    public String saveTiff(BufferedImage img, int documentId, int pageNumber) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "TIFF", baos);
        byte[] imageBytes = baos.toByteArray();

        String sql = "INSERT INTO dbo.Page (DocumentId, PageNumber, FilePath, Image) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            stmt.setInt(2, pageNumber);
            stmt.setString(3, "db://document/" + documentId + "/page/" + pageNumber);
            stmt.setBytes(4, imageBytes);

            stmt.executeUpdate();
        }

        return "db://document/" + documentId + "/page/" + pageNumber;
    }
}
