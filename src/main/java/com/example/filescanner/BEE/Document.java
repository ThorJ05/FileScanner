package com.example.filescanner.BEE;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private final List<ScannedFile> pages = new ArrayList<>();

    public void addPage(ScannedFile file) {
        pages.add(file);
    }

    public List<ScannedFile> getPages() {
        return pages;
    }
}
