package com.example.filescanner.BEE;

public class Profile {
    private int id;
    private String name;

    private int rotation;        // 0, 90, 180, 270
    private float brightness;    // 1.0 = normal
    private float contrast;      // 1.0 = normal
    private boolean autoCrop;
    private boolean splitOnBarcode;

    private String exportFormat; // "TIFF", "PNG", "JPG"
}
