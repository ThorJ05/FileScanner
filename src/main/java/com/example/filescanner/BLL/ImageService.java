package com.example.filescanner.BLL;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class ImageService {

    public Image toFxImage(BufferedImage img) {
        return SwingFXUtils.toFXImage(img, null);
    }
}
