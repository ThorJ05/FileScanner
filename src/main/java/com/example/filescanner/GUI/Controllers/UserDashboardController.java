package com.example.filescanner.GUI.Controllers;

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

import java.util.List;

public class UserDashboardController {

    // Services
    private final ScanManager scanManager = new ScanManager();
    private final ImageService imageService = new ImageService();

    // UI elements from your existing dashboard
    @FXML private Label welcomeLabel;
    @FXML private Label docCountLabel;
    @FXML private Label fileCountLabel;
    @FXML private Label activeProfileLabel;

    // New UI elements for scanning
    @FXML private ImageView imagePreview;
    @FXML private ListView<String> fileListView;
    @FXML private Label statusLabel;
    @FXML private Label sessionCountLabel;

    // Data
    private final ObservableList<String> fileLabels = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadUserInfo();
        loadStats();

        // Setup list
        if (fileListView != null) {
            fileListView.setItems(fileLabels);

            fileListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    showSelectedFile(newVal);
                }
            });
        }
    }

    // -------------------------
    // Existing dashboard logic
    // -------------------------

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
    private void onBack() {
        SceneController.goBack();
    }

    @FXML
    private void onLogout() {
        SceneController.setCurrentUser(null);
        SceneController.clearHistory();
        SceneController.switchTo("Login.fxml");
    }

    // -------------------------
    // NEW: Scanning logic
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

            // Add label to list
            fileLabels.add(last.getLabel());

            // Show image
            imagePreview.setImage(imageService.toFxImage(last.getImage()));

            // Update counters
            fileCountLabel.setText(String.valueOf(fileLabels.size()));
            sessionCountLabel.setText("Scanned this session: " + scanManager.getSessionScanCount());

            // Status
            if (last.hasBarcode()) {
                statusLabel.setText("Scanned: " + last.getLabel() + " (BARCODE FOUND: " + last.getBarcode() + ")");
            } else {
                statusLabel.setText("Scanned: " + last.getLabel());
            }

        } catch (Exception e) {
            statusLabel.setText("Error scanning file.");
            e.printStackTrace();
        }
    }

    private void showSelectedFile(String label) {
        scanManager.getCurrentBox().getDocuments().forEach(doc -> {
            doc.getPages().forEach(file -> {
                if (file.getLabel().equals(label)) {
                    imagePreview.setImage(imageService.toFxImage(file.getImage()));
                }
            });
        });
    }

    @FXML
    private void onReset() {
        scanManager.reset();
        fileLabels.clear();
        imagePreview.setImage(null);
        sessionCountLabel.setText("Scanned this session: 0");
        fileCountLabel.setText("0");
        statusLabel.setText("Session reset.");
    }
}
