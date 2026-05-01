package com.example.filescanner.BLL;


import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * ImageProcessor is a pure image-transform utility. It no longer depends on
 * the Profile model — callers supply rotation/brightness/contrast explicitly.
 * This reduces coupling between BLL and BEE.
 */

public class ImageProcessor {

    /**
     * Applies a Profile's settings to a BufferedImage.
     * High cohesion: this class ONLY handles image processing.
     * Low coupling: depends only on Java's built-in image classes + Profile model.
     */
    /**
     * Apply transforms to source image using explicit parameters.
     * rotationDegrees: 0..360
     * brightness: additive offset (same semantics as before)
     * contrast: multiplier
     */
    public BufferedImage applyProfile(BufferedImage source, int rotationDegrees, float brightness, float contrast) {
        BufferedImage result = source;

        result = applyRotation(result, rotationDegrees);
        result = applyBrightnessContrast(result, brightness, contrast);

        return result;
    }

    // ---------------------------
    //  PRIVATE HELPER METHODS
    // ---------------------------

    private BufferedImage applyRotation(BufferedImage img, int degrees) {
        if (degrees % 360 == 0) {
            return img;
        }

        double theta = Math.toRadians(degrees);

        // Compute bounds of rotated image
        double cos = Math.abs(Math.cos(theta));
        double sin = Math.abs(Math.sin(theta));
        int w = img.getWidth();
        int h = img.getHeight();
        int newW = (int) Math.floor(w * cos + h * sin);
        int newH = (int) Math.floor(h * cos + w * sin);

        // Create transform: translate to center of new image, rotate around center
        AffineTransform transform = new AffineTransform();
        transform.translate((newW - w) / 2.0, (newH - h) / 2.0);
        transform.rotate(theta, w / 2.0, h / 2.0);

        // Apply with destination image to avoid clipping
        BufferedImage rotated = new BufferedImage(newW, newH, img.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : img.getType());
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        op.filter(img, rotated);
        return rotated;
    }

    private BufferedImage applyBrightnessContrast(BufferedImage img, float brightness, float contrast) {
        RescaleOp op = new RescaleOp(contrast, brightness, null);
        return op.filter(img, null);
    }
}
