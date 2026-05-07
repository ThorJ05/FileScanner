package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Box;
import com.example.filescanner.BEE.Document;
import com.example.filescanner.BEE.ScannedFile;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BLL.ImageService;
import com.example.filescanner.BLL.ScanManager;
import com.example.filescanner.DAL.DocumentRepository;
import com.example.filescanner.DAL.PageRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

import java.util.List;

public class UserDashboardController {

    private ScanManager scanManager;
    private final ImageService imageService = new ImageService();

    private final DocumentRepository docRepo = new DocumentRepository();
    private final PageRepository pageRepo = new PageRepository();

    private int currentUserId;

    // Dashboard labels
    @FXML private Label welcomeLabel;
    @FXML private Label docCountLabel;
    @FXML private Label fileCountLabel;
    @FXML private Label activeProfileLabel;

    // Scanning UI
    @FXML private ImageView imagePreview;
    @FXML private Label statusLabel;
    @FXML private Label sessionCountLabel;

    // Lists
    @FXML private ListView<String> documentListView;
    @FXML private ListView<String> pageListView;

    public void initialize() {
        System.out.println("UserDashboardController.initialize() called");

        User user = SceneController.getCurrentUser();
        if (user == null) return;

        currentUserId = Integer.parseInt(user.getId()); // user.getId() ER allerede int

        loadUserInfo();
        loadStats();

        try {
            scanManager = new ScanManager(currentUserId);

            Box box = scanManager.getCurrentBox();

            // HENT DOKUMENTER FRA DB
            List<Document> docs = docRepo.getDocumentsByBoxId(box.getId());

            for (Document d : docs) {
                // HENT SIDER FRA DB
                List<ScannedFile> pages = pageRepo.getPagesByDocumentId(d.getId());
                for (ScannedFile p : pages) {
                    d.addPage(p);
                }
                box.addDocument(d);
            }

            updateDocumentListView();
            setupDocumentClick();
            setupPageClick();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // UPDATE DOCUMENT LIST + AUTO SELECT
    // -------------------------
    private void updateDocumentListView() {
        documentListView.getItems().clear();

        List<Document> docs = scanManager.getAllDocuments();
        int index = 1;

        for (Document doc : docs) {
            documentListView.getItems().add("Document " + index++);
        }

        docCountLabel.setText(String.valueOf(docs.size()));

        // AUTO-VÆLG DET NYESTE DOKUMENT
        if (!docs.isEmpty()) {
            documentListView.getSelectionModel().select(docs.size() - 1);
            updatePageListForSelectedDocument();
        }
    }

    // -------------------------
    // UPDATE PAGE LIST FOR SELECTED DOCUMENT
    // -------------------------
    private void updatePageListForSelectedDocument() {
        pageListView.getItems().clear();

        int docIndex = documentListView.getSelectionModel().getSelectedIndex();
        if (docIndex < 0) return;

        Document selectedDoc = scanManager.getAllDocuments().get(docIndex);

        for (int i = 0; i < selectedDoc.getPages().size(); i++) {
            pageListView.getItems().add("Page " + (i + 1));
        }
    }

    // -------------------------
    // USER INFO + STATS
    // -------------------------
    private void loadUserInfo() {
        User user = SceneController.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome back, " + user.getUsername() + "!");
            activeProfileLabel.setText(user.getUsername());
        }
    }

    private void loadStats() {
        docCountLabel.setText("0");
        fileCountLabel.setText("0");
        sessionCountLabel.setText("Scanned this session: 0");
    }

    @FXML
    private void onDashboard() {
        loadUserInfo();
        loadStats();
    }

    @FXML
    private void onLogout() {
        SceneController.setCurrentUser(null);
        SceneController.clearHistory();
        SceneController.switchTo("Login.fxml");
    }

    // -------------------------
    // SCANNING LOGIC
    // -------------------------
    @FXML
    private void onScan() {
        if (scanManager == null) {
            statusLabel.setText("Scanner not ready.");
            return;
        }

        try {
            List<ScannedFile> newFiles = scanManager.scanNext();

            if (newFiles == null || newFiles.isEmpty()) {
                statusLabel.setText("No files scanned.");
                return;
            }

            ScannedFile last = newFiles.get(newFiles.size() - 1);

            imagePreview.setImage(imageService.toFxImage(last.getImage()));

            fileCountLabel.setText(String.valueOf(scanManager.getTotalFileCount()));
            sessionCountLabel.setText("Scanned this session: " + scanManager.getSessionScanCount());

            if (last.hasBarcode()) {
                statusLabel.setText("Scanned: " + last.getLabel() + " (BARCODE: " + last.getBarcode() + ")");
            } else {
                statusLabel.setText("Scanned: " + last.getLabel());
            }

            updateDocumentListView();

        } catch (Exception e) {
            statusLabel.setText("Error scanning file.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onReset() {
        if (scanManager == null) return;

        scanManager.reset();
        documentListView.getItems().clear();
        pageListView.getItems().clear();
        imagePreview.setImage(null);

        sessionCountLabel.setText("Scanned this session: 0");
        fileCountLabel.setText("0");
        docCountLabel.setText("0");

        statusLabel.setText("Session reset.");
    }

    // -------------------------
    // CLICK HANDLERS
    // -------------------------
    private void setupDocumentClick() {
        documentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updatePageListForSelectedDocument();
        });
    }

    private void setupPageClick() {
        pageListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            int docIndex = documentListView.getSelectionModel().getSelectedIndex();
            int pageIndex = pageListView.getSelectionModel().getSelectedIndex();

            List<Document> docs = scanManager.getAllDocuments();
            if (docIndex < 0 || docIndex >= docs.size()) return;

            Document selectedDoc = docs.get(docIndex);
            if (pageIndex < 0 || pageIndex >= selectedDoc.getPages().size()) return;

            ScannedFile selectedPage = selectedDoc.getPages().get(pageIndex);
            imagePreview.setImage(imageService.toFxImage(selectedPage.getImage()));
        });
    }
}
