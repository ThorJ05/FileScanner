package com.example.filescanner.BLL;


import com.example.filescanner.BEE.Profile;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ImageProcessor {

    /**
     * Applies a Profile's settings to a BufferedImage.
     * High cohesion: this class ONLY handles image processing.
     * Low coupling: depends only on Java's built-in image classes + Profile model.
     */
    public BufferedImage applyProfile(BufferedImage source, Profile profile) {
        BufferedImage result = source;

        result = applyRotation(result, profile.getRotation());
        result = applyBrightnessContrast(result, profile.getBrightness(), profile.getContrast());

        return result;
    }

    // ---------------------------
    //  PRIVATE HELPER METHODS
    // ---------------------------

    private BufferedImage applyRotation(BufferedImage img, int degrees) {
        if (degrees == 0) {
            return img;
        }

        AffineTransform transform = new AffineTransform();
        transform.rotate(
                Math.toRadians(degrees),
                img.getWidth() / 2.0,
                img.getHeight() / 2.0
        );

        AffineTransformOp op = new AffineTransformOp(
                transform,
                AffineTransformOp.TYPE_BILINEAR
        );

        return op.filter(img, null);
    }

    private BufferedImage applyBrightnessContrast(BufferedImage img, float brightness, float contrast) {
        RescaleOp op = new RescaleOp(contrast, brightness, null);
        return op.filter(img, null);
    }
}
