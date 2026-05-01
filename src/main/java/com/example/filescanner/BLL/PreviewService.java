package com.example.filescanner.BLL;

import com.example.filescanner.BEE.Profile;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class PreviewService {

    private final ImageProcessor imageProcessor = new ImageProcessor();

    public Image generatePreview(BufferedImage img, Profile p) {
        return process(img, p.getRotation(), p.getBrightness(), p.getContrast());
    }

    private Image process(BufferedImage img, int rotation, float brightness, float contrast) {
        BufferedImage processed = imageProcessor.applyProfile(img, rotation, brightness, contrast);
        return SwingFXUtils.toFXImage(processed, null);
    }
}
