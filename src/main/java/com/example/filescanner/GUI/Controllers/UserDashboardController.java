package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Document;
import com.example.filescanner.BEE.ScannedFile;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BLL.ImageService;
import com.example.filescanner.BLL.ScanManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardController {

    private final ScanManager scanManager = new ScanManager();
    private final ImageService imageService = new ImageService();

    // Dashboard labels
    @FXML private Label welcomeLabel;
    @FXML private Label docCountLabel;
    @FXML private Label fileCountLabel;
    @FXML private Label activeProfileLabel;

    // Scanning UI
    @FXML private ImageView imagePreview;
    @FXML private Label statusLabel;
    @FXML private Label sessionCountLabel;

    // NEW UI
    @FXML private ListView<String> documentListView;
    @FXML private ListView<String> pageListView;

    @FXML
    public void initialize() {
        loadUserInfo();
        loadStats();

        setupDocumentClick();
        setupPageClick();
    }

    private void loadUserInfo() {
        User user = SceneController.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome back, " + user.getFirstName() + "!");
        } else {
            welcomeLabel.setText("Welcome back!");
        }
    }

    private void loadStats() {
        docCountLabel.setText("0");
        fileCountLabel.setText("0");
        activeProfileLabel.setText("None");
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
        try {
            List<ScannedFile> newFiles = scanManager.scanNext();

            if (newFiles.isEmpty()) {
                statusLabel.setText("No files scanned.");
                return;
            }

            ScannedFile last = newFiles.get(newFiles.size() - 1);

            // Show image
            imagePreview.setImage(imageService.toFxImage(last.getImage()));

            // Update counters
            fileCountLabel.setText(String.valueOf(scanManager.getTotalFileCount()));
            sessionCountLabel.setText("Scanned this session: " + scanManager.getSessionScanCount());

            // Status
            if (last.hasBarcode()) {
                statusLabel.setText("Scanned: " + last.getLabel() + " (BARCODE FOUND: " + last.getBarcode() + ")");
            } else {
                statusLabel.setText("Scanned: " + last.getLabel());
            }

            updateDocumentList();

        } catch (Exception e) {
            statusLabel.setText("Error scanning file.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onReset() {
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
    // DOCUMENT + PAGE VIEW
    // -------------------------

    private void updateDocumentList() {
        documentListView.getItems().clear();

        List<Document> docs = new ArrayList<>(scanManager.getCurrentBox().getDocuments());

        // Include active document if not empty
        if (!scanManager.getCurrentDocument().getPages().isEmpty()) {
            docs.add(scanManager.getCurrentDocument());
        }

        int index = 1;
        for (Document doc : docs) {
            documentListView.getItems().add("Document " + index++);
        }

        docCountLabel.setText(String.valueOf(docs.size()));
    }

    private void setupDocumentClick() {
        documentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            int docIndex = documentListView.getSelectionModel().getSelectedIndex();

            List<Document> docs = new ArrayList<>(scanManager.getCurrentBox().getDocuments());
            if (!scanManager.getCurrentDocument().getPages().isEmpty()) {
                docs.add(scanManager.getCurrentDocument());
            }

            Document selectedDoc = docs.get(docIndex);

            pageListView.getItems().clear();
            for (int i = 0; i < selectedDoc.getPages().size(); i++) {
                pageListView.getItems().add("Page " + (i + 1));
            }
        });
    }

    private void setupPageClick() {
        pageListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            int docIndex = documentListView.getSelectionModel().getSelectedIndex();
            int pageIndex = pageListView.getSelectionModel().getSelectedIndex();

            List<Document> docs = new ArrayList<>(scanManager.getCurrentBox().getDocuments());
            if (!scanManager.getCurrentDocument().getPages().isEmpty()) {
                docs.add(scanManager.getCurrentDocument());
            }

            Document selectedDoc = docs.get(docIndex);
            ScannedFile selectedPage = selectedDoc.getPages().get(pageIndex);

            imagePreview.setImage(imageService.toFxImage(selectedPage.getImage()));
        });
    }
}
