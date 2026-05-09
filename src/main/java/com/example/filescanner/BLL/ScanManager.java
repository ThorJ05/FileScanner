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

        // Hent seneste box i stedet for at oprette en ny
        currentBox = boxRepo.getLatestBoxForUser(userId);

        if (currentBox == null) {
            // Hvis ingen box findes → opret én
            int boxId = boxRepo.createBox(userId);
            currentBox = new Box(boxId, userId);
        }

        currentDocument = null;
    }



    // MAIN SCAN METHOD

    public List<ScannedFile> scanNext() throws Exception {
        List<ScannedFile> result = new ArrayList<>();

        // Hent TIFF
        BufferedImage img = api.fetchRandomTiff();
        String barcode = barcodeService.readBarcode(img);

        sessionScanCount++;
        totalFileCount++;


        if (barcode != null) {
            int newDocId = docRepo.createDocument(currentBox.getId(), barcode);
            currentDocument = new Document(newDocId, currentBox.getId(), barcode);
            currentBox.addDocument(currentDocument);
        }

// if no documents yet new barcode
        if (currentDocument == null) {
            String safeBarcode = (barcode == null ? "NO_BARCODE" : barcode);

            int newDocId = docRepo.createDocument(currentBox.getId(), safeBarcode);
            currentDocument = new Document(newDocId, currentBox.getId(), safeBarcode);
            currentBox.addDocument(currentDocument);
        }


        // Page number
        int pageNumber = currentDocument.getPages().size() + 1;

        // safe TIFF
        String filePath = fileRepo.saveTiff(img, currentDocument.getId(), pageNumber);

        // SAFE METADATA IN DATABASE
        pageRepo.createPage(currentDocument.getId(), pageNumber, filePath);

        ScannedFile scanned = new ScannedFile("Page " + pageNumber, img, barcode, filePath);
        currentDocument.addPage(scanned);

        result.add(scanned);
        return result;
    }


    // GETTERS FOR CONTROLLER

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


    // RESET


    public void reset() {
        currentBox.clearDocuments();
        currentDocument = null;
        sessionScanCount = 0;
        totalFileCount = 0;
    }
}
