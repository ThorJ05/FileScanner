package com.example.filescanner.BLL;

import com.example.filescanner.API.TiffApiClient;
import com.example.filescanner.BEE.Box;
import com.example.filescanner.BEE.Document;
import com.example.filescanner.BEE.ScannedFile;
import com.example.filescanner.DAL.BoxRepository;
import com.example.filescanner.DAL.DocumentRepository;
import com.example.filescanner.DAL.FileRepository;
import com.example.filescanner.DAL.PageRepository;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ScanManager {

    private final TiffApiClient api = new TiffApiClient();
    private final BarcodeService barcodeService = new BarcodeService();

    private final BoxRepository boxRepo = new BoxRepository();
    private final DocumentRepository docRepo = new DocumentRepository();
    private final PageRepository pageRepo = new PageRepository();
    private final FileRepository fileRepo = new FileRepository();

    private Box currentBox;
    private Document currentDocument;

    private int sessionScanCount = 0;
    private int totalFileCount = 0;

    public ScanManager(int userId) throws Exception {
        int boxId = boxRepo.createBox(userId);
        currentBox = new Box(boxId, userId);
        currentDocument = new Document(-1, boxId, null); // midlertidigt dokument
    }

    // -------------------------
    // MAIN SCAN METHOD
    // -------------------------
    public List<ScannedFile> scanNext() throws Exception {
        List<ScannedFile> result = new ArrayList<>();

        BufferedImage img = api.fetchRandomTiff();
        String barcode = barcodeService.readBarcode(img);

        sessionScanCount++;
        totalFileCount++;

        // Hvis barcode → nyt dokument
        if (barcode != null) {
            int newDocId = docRepo.createDocument(currentBox.getId(), barcode);
            currentDocument = new Document(newDocId, currentBox.getId(), barcode);
            currentBox.addDocument(currentDocument);
        }

        // Hvis ingen dokument endnu → opret et
        if (currentDocument.getId() == -1) {
            int newDocId = docRepo.createDocument(currentBox.getId(), null);
            currentDocument = new Document(newDocId, currentBox.getId(), null);
            currentBox.addDocument(currentDocument);
        }

        // Gem TIFF på disk
        String filePath = fileRepo.saveTiff(img, currentDocument.getId(), currentDocument.getPages().size() + 1);

        // Gem metadata i DB
        pageRepo.createPage(currentDocument.getId(), currentDocument.getPages().size() + 1, filePath);

        // Tilføj til RAM-model
        ScannedFile scanned = new ScannedFile("Page " + totalFileCount, img, barcode, filePath);
        currentDocument.addPage(scanned);

        result.add(scanned);
        return result;
    }

    // -------------------------
    // GETTERS FOR CONTROLLER
    // -------------------------

    public int getTotalFileCount() {
        return totalFileCount;
    }

    public int getSessionScanCount() {
        return sessionScanCount;
    }

    public Box getCurrentBox() {
        return currentBox;
    }

    public Document getCurrentDocument() {
        return currentDocument;
    }

    public List<Document> getAllDocuments() {
        return currentBox.getDocuments();
    }

    // -------------------------
    // RESET
    // -------------------------

    public void reset() {
        currentBox.clearDocuments();
        currentDocument = new Document(-1, currentBox.getId(), null);
        sessionScanCount = 0;
        totalFileCount = 0;
    }
}
