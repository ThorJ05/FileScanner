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

    public void createPage(int documentId, int pageNumber, String filePath) {
        System.out.println("Page saved: document=" + documentId + " page=" + pageNumber);
    }

    public List<ScannedFile> getPagesByDocumentId(int documentId) throws Exception {

        List<ScannedFile> pages = new ArrayList<>();

        String sql = "SELECT PageNumber, Image FROM dbo.Page WHERE DocumentId = ? ORDER BY PageNumber";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                int pageNumber = rs.getInt("PageNumber");
                byte[] imageBytes = rs.getBytes("Image");

                if (imageBytes == null) continue;

                BufferedImage img = decodeTiff(imageBytes);
                if (img == null) continue;

                String ref = "db://document/" + documentId + "/page/" + pageNumber;

                pages.add(new ScannedFile(
                        "Page " + pageNumber,
                        img,
                        null,
                        ref
                ));
            }
        }

        return pages;
    }

    public void updatePageNumber(int documentId, int newPageNumber, String filePath) throws Exception {

        String sql = "UPDATE dbo.Page SET PageNumber = ? " +
                "WHERE DocumentId = ? AND PageNumber = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newPageNumber);
            stmt.setInt(2, documentId);

            String[] parts = filePath.split("/");
            int oldPageNumber = Integer.parseInt(parts[parts.length - 1]);

            stmt.setInt(3, oldPageNumber);

            stmt.executeUpdate();
        }
    }

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