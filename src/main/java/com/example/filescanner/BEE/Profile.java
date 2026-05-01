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
    // Used by repository (loading from DB)
    public Profile(int id, String name, int rotation, float brightness, float contrast,
                   boolean splitOnBarcode, String exportFormat) {
        this.id = id;
        this.name = name;
        this.rotation = rotation;
        this.brightness = brightness;
        this.contrast = contrast;
        this.splitOnBarcode = splitOnBarcode;
        this.exportFormat = exportFormat;
    }

    // Used by GUI (creating new profiles)
    public Profile(String name, int rotation, float brightness, float contrast,
                   boolean splitOnBarcode, String exportFormat) {
        this.name = name;
        this.rotation = rotation;
        this.brightness = brightness;
        this.contrast = contrast;
        this.splitOnBarcode = splitOnBarcode;
        this.exportFormat = exportFormat;
    }


    // GETTERS
    public int getId() { return id; }
    public String getName() { return name; }
    public int getRotation() { return rotation; }
    public float getBrightness() { return brightness; }
    public float getContrast() { return contrast; }
    public boolean isSplitOnBarcode() { return splitOnBarcode; }
    public boolean isAutoCrop() { return autoCrop; }
    public String getExportFormat() { return exportFormat; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRotation(int rotation) { this.rotation = rotation; }
    public void setBrightness(float brightness) { this.brightness = brightness; }
    public void setContrast(float contrast) { this.contrast = contrast; }
    public void setSplitOnBarcode(boolean splitOnBarcode) { this.splitOnBarcode = splitOnBarcode; }
    public void setAutoCrop(boolean autoCrop) { this.autoCrop = autoCrop; }
    public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }
}
