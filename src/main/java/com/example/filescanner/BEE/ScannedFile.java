package com.example.filescanner.BEE;

import java.awt.image.BufferedImage;

public class ScannedFile {

    private final String label;
    private final BufferedImage image;
    private final String barcode;
    private final String filePath;

    private int referenceId;   // ← NYT
    private int rotation = 0;

    public ScannedFile(String label, BufferedImage image, String barcode, String filePath) {
        this.label = label;
        this.image = image;
        this.barcode = barcode;
        this.filePath = filePath;
    }

    public String getLabel() { return label; }
    public BufferedImage getImage() { return image; }
    public String getBarcode() { return barcode; }
    public String getFilePath() { return filePath; }

    public boolean hasBarcode() {
        return barcode != null;
    }

    public int getRotation() { return rotation; }
    public void setRotation(int rotation) { this.rotation = rotation; }

    // ⭐ Reference ID
    public int getReferenceId() { return referenceId; }
    public void setReferenceId(int referenceId) { this.referenceId = referenceId; }
}
