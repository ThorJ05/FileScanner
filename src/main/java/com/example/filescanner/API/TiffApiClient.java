package com.example.filescanner.API;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TiffApiClient {

    private static final String BASE_URL =
            "https://studentiffapi-production.up.railway.app";


    public BufferedImage fetchRandomTiff() throws Exception {
        URL url = new URL(BASE_URL + "/getRandomFile");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream is = conn.getInputStream()) {
            return ImageIO.read(is); // kræver TwelveMonkeys
        }
    }
}
