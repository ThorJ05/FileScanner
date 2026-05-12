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
    private int scanCounter = 0;

    // ---------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------
    public ScanManager(int userId) throws Exception {
        currentBox = boxRepo.getOrCreateBox(userId);
        currentDocument = null;
    }

    // ---------------------------------------------------
    // MAIN ASYNC-FRIENDLY SCAN METHOD
    // ---------------------------------------------------
    public ScannedFile scanNextAsync() throws Exception {

        BufferedImage img = safeFetchTiff();
        String barcode = barcodeService.readBarcode(img);

        sessionScanCount++;
        totalFileCount++;

        ensureCurrentDocument(barcode);

        int pageNumber = currentDocument.getPages().size() + 1;

        // GUI needs the image immediately
        ScannedFile scanned = new ScannedFile("Page " + pageNumber, img, barcode, null);
        currentDocument.addPage(scanned);

        savePageAsync(img, pageNumber, scanned);

        return scanned;
    }

    // ---------------------------------------------------
    // SAFE TIFF FETCH (RETRY)
    // ---------------------------------------------------
    private BufferedImage safeFetchTiff() throws Exception {
        for (int i = 0; i < 3; i++) {
            try {
                return api.fetchRandomTiff();
            } catch (Exception e) {
                System.out.println("Retry TIFF fetch (" + (i + 1) + "/3)");
                Thread.sleep(150);
            }
        }
        throw new Exception("Failed to fetch TIFF after 3 attempts");
    }

    // ---------------------------------------------------
    // DOCUMENT CREATION LOGIC
    // ---------------------------------------------------
    private void ensureCurrentDocument(String barcode) throws Exception {

        if (barcode != null) {
            int id = docRepo.createDocument(currentBox.getId(), barcode);
            currentDocument = new Document(id, currentBox.getId(), barcode);
            currentBox.addDocument(currentDocument);
            return;
        }

        if (currentDocument == null) {
            String safe = (barcode == null ? "NO_BARCODE" : barcode);
            int id = docRepo.createDocument(currentBox.getId(), safe);
            currentDocument = new Document(id, currentBox.getId(), safe);
            currentBox.addDocument(currentDocument);
        }
    }

    // ---------------------------------------------------
    // BACKGROUND SAVE
    // ---------------------------------------------------
    private void savePageAsync(BufferedImage img, int pageNumber, ScannedFile scanned) {
        new Thread(() -> {
            try {
                String filePath = fileRepo.saveTiff(img, currentDocument.getId(), pageNumber);
                byte[] bytes = fileRepo.readBytes(filePath);

                int refId = ++scanCounter;

                pageRepo.createPage(
                        currentDocument.getId(),
                        pageNumber,
                        filePath,
                        refId,
                        bytes
                );

                scanned.setReferenceId(refId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ---------------------------------------------------
    // RESET
    // ---------------------------------------------------
    public void reset(int userId) throws Exception {
        currentBox = boxRepo.createNewBox(userId);
        currentDocument = null;
        sessionScanCount = 0;
        totalFileCount = 0;
        scanCounter = 0;
    }

    // ---------------------------------------------------
    // GETTERS
    // ---------------------------------------------------
    public Box getCurrentBox() { return currentBox; }

    public List<Document> getAllDocuments() { return currentBox.getDocuments(); }

    public int getTotalFileCount() { return totalFileCount; }

    public int getSessionScanCount() { return sessionScanCount; }

    public void setCurrentDocument(Document doc) {
        this.currentDocument = doc;
    }
}
