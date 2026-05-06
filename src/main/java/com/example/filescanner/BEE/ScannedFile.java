package com.example.filescanner.BEE;

import java.awt.image.BufferedImage;

public class ScannedFile {

    private final int scanOrder;       // 1, 2, 3 etc — resets each session
    private final String label;        // "File_001", "File_002" etc
    private final BufferedImage image; // the actual image from the API
    private int rotation;              // rotation applied in UI

    public ScannedFile(int scanOrder, BufferedImage image) {
        this.scanOrder = scanOrder;
        this.label     = String.format("File_%03d", scanOrder);
        this.image     = image;
        this.rotation  = 0;
    }

    public int getScanOrder()       { return scanOrder; }
    public String getLabel()        { return label; }
    public BufferedImage getImage() { return image; }
    public int getRotation()        { return rotation; }
    public void setRotation(int r)  { this.rotation = r; }

    @Override
    public String toString() { return label; }
}