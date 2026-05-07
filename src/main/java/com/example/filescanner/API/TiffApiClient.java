package com.example.filescanner.API;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TiffApiClient {

    private static final String BASE_URL =
            "https://studentiffapi-production.up.railway.app";

    public BufferedImage fetchRandomTiff() throws Exception {

        URL url = new URL(BASE_URL + "/getRandomFile");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // ZIP input stream
        ZipInputStream zipIn = new ZipInputStream(conn.getInputStream());
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".tiff")) {

                // Read TIFF file from ZIP
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read;

                while ((read = zipIn.read(buffer)) != -1) {
                    baos.write(buffer, 0, read);
                }

                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                // Convert to BufferedImage
                return ImageIO.read(bais);
            }

            entry = zipIn.getNextEntry();
        }

        return null;
    }
}
