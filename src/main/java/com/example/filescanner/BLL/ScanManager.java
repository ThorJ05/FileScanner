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

        // Hvis der er barcode → afslut dokument og start nyt
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

    // Returnerer ALLE dokumenter (inkl. det aktive)
    public List<Document> getAllDocuments() {
        List<Document> docs = new ArrayList<>(currentBox.getDocuments());

        if (!currentDocument.getPages().isEmpty()) {
            docs.add(currentDocument);
        }

        return docs;
    }

    public int getSessionScanCount() {
        return sessionCount;
    }

    public int getTotalFileCount() {
        return currentBox.getDocuments()
                .stream()
                .mapToInt(doc -> doc.getPages().size())
                .sum()
                + currentDocument.getPages().size();
    }

    public Box getCurrentBox() {
        return currentBox;
    }

    public Document getCurrentDocument() {
        return currentDocument;
    }

    public void reset() {
        // Tilføj sidste dokument hvis det ikke er tomt
        if (!currentDocument.getPages().isEmpty()) {
            currentBox.addDocument(currentDocument);
        }

        sessionCount = 0;
        currentDocument = new Document();
        currentBox.getDocuments().clear();
    }
}
