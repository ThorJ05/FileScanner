package com.example.filescanner.BLL;

import com.example.filescanner.API.TiffApiClient;
import com.example.filescanner.BEE.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ScanManager {

    private final TiffApiClient api = new TiffApiClient();
    private final BarcodeService barcodeService = new BarcodeService();

    private final Box currentBox = new Box();
    private Document currentDocument = new Document();

    private int sessionCount = 0;

    public List<ScannedFile> scanNext() throws Exception {
        List<ScannedFile> result = new ArrayList<>();

        BufferedImage img = api.fetchRandomTiff();
        String barcode = barcodeService.readBarcode(img);

        ScannedFile file = new ScannedFile("Page " + (sessionCount + 1), img, barcode);
        sessionCount++;

        // Hvis der er barcode → nyt dokument
        if (file.hasBarcode()) {
            if (!currentDocument.getPages().isEmpty()) {
                currentBox.addDocument(currentDocument);
            }
            currentDocument = new Document();
        }

        currentDocument.addPage(file);
        result.add(file);

        return result;
    }

    public int getSessionScanCount() {
        return sessionCount;
    }

    public Box getCurrentBox() {
        return currentBox;
    }

    public void reset() {
        sessionCount = 0;
        currentDocument = new Document();
        currentBox.getDocuments().clear();
    }
}
