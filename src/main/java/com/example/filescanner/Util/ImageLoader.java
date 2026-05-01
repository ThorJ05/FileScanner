package com.example.filescanner.Util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Small, focused utility for loading images (including TIFF) using ImageIO.
 * Keeps image-reading logic out of controllers so controllers stay cohesive.
 */
public final class ImageLoader {

    private ImageLoader() { /* no instantiation */ }

    /**
     * Attempts to load an image from file. First uses ImageIO.read, then
     * falls back to ImageReaders (useful for TIFF when plugins are present).
     * Returns the first image/frame or null if none could be read.
     */
    public static BufferedImage loadImage(File file) throws IOException {
        // Fast path
        BufferedImage img = ImageIO.read(file);
        if (img != null) return img;

        // Fallback: use ImageReaders (handles multi-format plugins like TwelveMonkeys)
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            if (iis == null) return null;
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    return reader.read(0);
                } finally {
                    reader.dispose();
                }
            }
        }

        return null;
    }
}
