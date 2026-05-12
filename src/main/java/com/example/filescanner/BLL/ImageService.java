package com.example.filescanner.BLL;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class ImageService {

    public Image toFxImage(BufferedImage img) {
        return SwingFXUtils.toFXImage(img, null);
    }

    // ✅ Added rotation helper
    public BufferedImage applyRotation(BufferedImage img, double degrees) {
        double radians = Math.toRadians(degrees);
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage rotated = new BufferedImage(w, h, img.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.rotate(radians, w / 2.0, h / 2.0);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public BufferedImage decodeImage(byte[] bytes) throws Exception {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

}
