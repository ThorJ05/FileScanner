package com.example.filescanner.Util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public final class ImageLoader {

    private ImageLoader() {}

    public static BufferedImage loadTiff(File file) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            if (iis == null) return null;

            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("tiff");
            if (!readers.hasNext()) return null;

            ImageReader reader = readers.next();
            try {
                reader.setInput(iis);
                return reader.read(0);
            } finally {
                reader.dispose();
            }
        }
    }

}
