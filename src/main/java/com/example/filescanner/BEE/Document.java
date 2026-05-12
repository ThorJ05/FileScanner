package com.example.filescanner.BEE;

import java.util.ArrayList;
import java.util.List;

public class Document {

    private int id;
    private int boxId;
    private String barcode;
    private List<ScannedFile> pages = new ArrayList<>();

    // LAZY LOADING FLAG
    private boolean pagesLoaded = false;

    public Document(int id, int boxId, String barcode) {
        this.id = id;
        this.boxId = boxId;
        this.barcode = barcode;
    }

    public int getId() {
        return id;
    }

    public int getBoxId() {
        return boxId;
    }

    public String getBarcode() {
        return barcode;
    }

    public List<ScannedFile> getPages() {
        return pages;
    }

    public void addPage(ScannedFile page) {
        pages.add(page);
    }

    // LAZY LOADING GET/SET
    public boolean isPagesLoaded() {
        return pagesLoaded;
    }

    public void setPagesLoaded(boolean pagesLoaded) {
        this.pagesLoaded = pagesLoaded;
    }
}
