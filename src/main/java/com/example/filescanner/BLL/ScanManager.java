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
    private int scanCounter = 0;

    // ---------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------
    public ScanManager(int userId) throws Exception {
        currentBox = boxRepo.getOrCreateBox(userId);
        currentDocument = null; // GUI sætter den rigtige senere
    }

    // ---------------------------------------------------
    // MAIN SCAN METHOD (OPTIMERET)
    // ---------------------------------------------------
    public List<ScannedFile> scanNext() throws Exception {
        List<ScannedFile> result = new ArrayList<>();

        // 1. Fetch TIFF
        BufferedImage img = api.fetchRandomTiff();
        String barcode = barcodeService.readBarcode(img);

        sessionScanCount++;
        totalFileCount++;

        // 2. Create new document if barcode found
        if (barcode != null) {
            int newDocId = docRepo.createDocument(currentBox.getId(), barcode);
            currentDocument = new Document(newDocId, currentBox.getId(), barcode);
            currentBox.addDocument(currentDocument);
        }

        // 3. If no document exists yet (first scan)
        if (currentDocument == null) {
            String safeBarcode = (barcode == null ? "NO_BARCODE" : barcode);
            int newDocId = docRepo.createDocument(currentBox.getId(), safeBarcode);
            currentDocument = new Document(newDocId, currentBox.getId(), safeBarcode);
            currentBox.addDocument(currentDocument);
        }

        int pageNumber = currentDocument.getPages().size() + 1;

        // 4. Save TIFF to disk
        String filePath = fileRepo.saveTiff(img, currentDocument.getId(), pageNumber);

        // 5. Read TIFF bytes directly from disk (FAST)
        byte[] imageBytes = fileRepo.readBytes(filePath);

        int referenceId = ++scanCounter;

        // 6. Save page metadata + image bytes
        pageRepo.createPage(
                currentDocument.getId(),
                pageNumber,
                filePath,
                referenceId,
                imageBytes
        );

        // 7. Create ScannedFile WITHOUT image (lazy loading)
        ScannedFile scanned = new ScannedFile("Page " + pageNumber, img, barcode, filePath);

        scanned.setReferenceId(referenceId);

        currentDocument.addPage(scanned);
        result.add(scanned);

        return result;
    }

    // ---------------------------------------------------
    // RESET → NEW BOX
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
