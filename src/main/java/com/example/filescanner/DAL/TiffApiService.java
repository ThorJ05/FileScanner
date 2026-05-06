package com.example.filescanner.DAL;

import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TiffApiService {

    private static final String BASE_URL = "https://studentiffapi-production.up.railway.app";

    static {
        IIORegistry.getDefaultInstance()
                .registerServiceProvider(new TIFFImageReaderSpi());
        ImageIO.scanForPlugins();
    }

    public List<BufferedImage> fetchRandomFile() throws Exception {
        URL url = new URL(BASE_URL + "/getRandomFile");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);

        if (conn.getResponseCode() != 200) {
            throw new Exception("API returned HTTP " + conn.getResponseCode());
        }

        byte[] responseBytes;
        try (InputStream is = conn.getInputStream()) {
            responseBytes = is.readAllBytes();
        }

        System.out.println("Received bytes: " + responseBytes.length);

        // Check if it's a ZIP file (starts with PK)
        if (responseBytes[0] == 'P' && responseBytes[1] == 'K') {
            System.out.println("Detected ZIP file — extracting TIFFs...");
            return extractTiffsFromZip(responseBytes);
        } else {
            System.out.println("Detected raw TIFF — reading directly...");
            BufferedImage image = readTiff(responseBytes);
            return List.of(image);
        }
    }

    private List<BufferedImage> extractTiffsFromZip(byte[] zipBytes) throws Exception {
        List<BufferedImage> images = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(
                new ByteArrayInputStream(zipBytes))) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName().toLowerCase();
                System.out.println("ZIP entry: " + entry.getName());

                if (name.endsWith(".tif") || name.endsWith(".tiff")) {
                    byte[] tiffBytes = zis.readAllBytes();
                    System.out.println("Reading TIFF: " + entry.getName()
                            + " (" + tiffBytes.length + " bytes)");
                    BufferedImage image = readTiff(tiffBytes);
                    if (image != null) {
                        images.add(image);
                    }
                }
                zis.closeEntry();
            }
        }

        if (images.isEmpty()) {
            throw new Exception("No TIFF files found inside ZIP");
        }

        System.out.println("Extracted " + images.size() + " image(s) from ZIP");
        return images;
    }

    private BufferedImage readTiff(byte[] tiffBytes) throws Exception {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("tiff");

        if (!readers.hasNext()) {
            throw new Exception("No TIFF reader registered!");
        }

        ImageReader reader = readers.next();

        try (ImageInputStream iis = ImageIO.createImageInputStream(
                new ByteArrayInputStream(tiffBytes))) {

            reader.setInput(iis, false);
            BufferedImage image = reader.read(0);
            System.out.println("Image decoded: "
                    + image.getWidth() + "x" + image.getHeight());
            return image;

        } finally {
            reader.dispose();
        }
    }

    public int fetchCount() throws Exception {
        URL url = new URL(BASE_URL + "/getCount");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if (conn.getResponseCode() != 200) {
            throw new Exception("API returned HTTP " + conn.getResponseCode());
        }

        try (InputStream is = conn.getInputStream()) {
            String body = new String(is.readAllBytes()).trim();
            return Integer.parseInt(body);
        }
    }
}