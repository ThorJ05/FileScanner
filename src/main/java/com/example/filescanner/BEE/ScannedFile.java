package com.example.filescanner.BEE;

import java.awt.image.BufferedImage;

public class ScannedFile {

    private final String label;
    private final BufferedImage image;
    private final String barcode;
    private final String filePath; // <-- MANGLEDE

    public ScannedFile(String label, BufferedImage image, String barcode, String filePath) {
        this.label = label;
        this.image = image;
        this.barcode = barcode;
        this.filePath = filePath; // <-- MANGLEDE
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

    public String getFilePath() {
        return filePath; // <-- NU VIRKER DET
    }
}
