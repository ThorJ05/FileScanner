package com.example.filescanner.BEE;

public class Profile {

    private int id;
    private String name;

    private int rotation;
    private float brightness;
    private float contrast;
    private boolean autoCrop;
    private boolean splitOnBarcode;

    private String exportFormat;

    // Used by repository
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

    // EMPTY
    public Profile() {}

    // SIMPLE constructor (used by soft delete table)
    public Profile(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // GETTERS + SETTERS (unchanged)
    public int getId() { return id; }
    public String getName() { return name; }
    public int getRotation() { return rotation; }
    public float getBrightness() { return brightness; }
    public float getContrast() { return contrast; }
    public boolean isSplitOnBarcode() { return splitOnBarcode; }
    public boolean isAutoCrop() { return autoCrop; }
    public String getExportFormat() { return exportFormat; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRotation(int rotation) { this.rotation = rotation; }
    public void setBrightness(float brightness) { this.brightness = brightness; }
    public void setContrast(float contrast) { this.contrast = contrast; }
    public void setSplitOnBarcode(boolean splitOnBarcode) { this.splitOnBarcode = splitOnBarcode; }
    public void setAutoCrop(boolean autoCrop) { this.autoCrop = autoCrop; }
    public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }

    @Override
    public String toString() {
        return name;
    }
}
