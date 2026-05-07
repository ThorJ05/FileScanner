package com.example.filescanner.BEE;

import java.awt.image.BufferedImage;

public class ScannedFile {
    private final String label;
    private final BufferedImage image;
    private final String barcode; // null hvis ingen barcode

    public ScannedFile(String label, BufferedImage image, String barcode) {
        this.label = label;
        this.image = image;
        this.barcode = barcode;
    }

    public String getLabel() {
        return label;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getBarcode() {
        return barcode;
    }

    public boolean hasBarcode() {
        return barcode != null;
    }
}
