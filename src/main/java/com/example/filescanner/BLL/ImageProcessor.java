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

    public BufferedImage applyProfile(BufferedImage source, int rotationDegrees, float brightness, float contrast) {
        BufferedImage result = source;

        result = applyRotation(result, rotationDegrees);
        result = applyBrightnessContrast(result, brightness, contrast);

        return result;
    }



    private BufferedImage applyRotation(BufferedImage img, int degrees) {
        degrees = ((degrees % 360) + 360) % 360; // normalize

        if (degrees == 0) {
            return img;
        }

        double theta = Math.toRadians(degrees);

        AffineTransform transform = new AffineTransform();
        transform.rotate(theta, img.getWidth() / 2.0, img.getHeight() / 2.0);

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
