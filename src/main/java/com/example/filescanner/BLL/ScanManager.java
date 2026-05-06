package com.example.filescanner.BLL;

import com.example.filescanner.BEE.ScannedFile;
import com.example.filescanner.DAL.TiffApiService;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanManager {

    private final TiffApiService apiService;
    private final List<ScannedFile> scannedFiles;
    private int scanCounter;

    public ScanManager() {
        this.apiService   = new TiffApiService();
        this.scannedFiles = new ArrayList<>();
        this.scanCounter  = 0;
    }

    // Fetches one file from the API — may return multiple images if ZIP
    public List<ScannedFile> scanNext() throws Exception {
        List<BufferedImage> images = apiService.fetchRandomFile();
        List<ScannedFile> newFiles = new ArrayList<>();

        for (BufferedImage image : images) {
            scanCounter++;
            ScannedFile file = new ScannedFile(scanCounter, image);
            scannedFiles.add(file);
            newFiles.add(file);
        }

        return newFiles;
    }

    public int getApiCount() throws Exception {
        return apiService.fetchCount();
    }

    public List<ScannedFile> getScannedFiles() {
        return Collections.unmodifiableList(scannedFiles);
    }

    public int getSessionScanCount() {
        return scanCounter;
    }

    public void reset() {
        scannedFiles.clear();
        scanCounter = 0;
    }
}