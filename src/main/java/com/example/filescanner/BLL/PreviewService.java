package com.example.filescanner.BLL;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class PreviewService {

    private final ImageProcessor processor = new ImageProcessor();

    public Image generatePreview(BufferedImage img, int rotation, float brightness, float contrast) {
        BufferedImage processed = processor.applyProfile(img, rotation, brightness, contrast);
        return SwingFXUtils.toFXImage(processed, null);
    }
}
