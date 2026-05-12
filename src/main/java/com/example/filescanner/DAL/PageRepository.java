package com.example.filescanner.DAL;

import com.example.filescanner.BEE.ScannedFile;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi;

import javax.imageio.*;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.*;

public class PageRepository {

    static {
        IIORegistry.getDefaultInstance()
                .registerServiceProvider(new TIFFImageReaderSpi());
        ImageIO.scanForPlugins();
    }

    // -----------------------------
    // CREATE PAGE (med reference_id)
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
    // LOAD PAGES (med reference_id)
    // -----------------------------
    public List<ScannedFile> getPagesByDocumentId(int documentId) throws Exception {

        List<ScannedFile> pages = new ArrayList<>();

        String sql = "SELECT PageNumber, Image, reference_id, FilePath " +
                "FROM dbo.Page WHERE DocumentId = ? ORDER BY reference_id";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                int pageNumber = rs.getInt("PageNumber");
                int referenceId = rs.getInt("reference_id");
                String filePath = rs.getString("FilePath");
                byte[] imageBytes = rs.getBytes("Image");

                if (imageBytes == null) continue;

                BufferedImage img = decodeTiff(imageBytes);
                if (img == null) continue;

                ScannedFile sf = new ScannedFile(
                        "Page " + pageNumber,
                        img,
                        null,
                        filePath
                );

                sf.setReferenceId(referenceId);

                pages.add(sf);
            }
        }

        return pages;
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

    // -----------------------------
    // TIFF DECODER
    // -----------------------------
    private BufferedImage decodeTiff(byte[] bytes) {
        try {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("tiff");
            if (!readers.hasNext()) return null;

            ImageReader reader = readers.next();

            try (ImageInputStream iis =
                         ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {

                reader.setInput(iis, false);
                return reader.read(0);

            } finally {
                reader.dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
