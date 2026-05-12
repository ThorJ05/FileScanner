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
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.util.List;

public class UserDashboardController {

    private ScanManager scanManager;
    private final ImageService imageService = new ImageService();
    private final DocumentRepository docRepo = new DocumentRepository();
    private final PageRepository pageRepo = new PageRepository();

    @FXML private Label welcomeLabel, docCountLabel, fileCountLabel, activeProfileLabel, statusLabel, sessionCountLabel;
    @FXML private ImageView imagePreview;
    @FXML private ListView<String> documentListView, pageListView;

    // ⭐ Rotation controls
    @FXML private Slider rotationSlider;
    @FXML private TextField rotationField;

    public void initialize() {
        User user = SceneController.getCurrentUser();
        if (user == null) return;

        int currentUserId = Integer.parseInt(user.getId());

        loadUserInfo(user);
        loadStats();

        try {
            scanManager = new ScanManager(currentUserId);
            loadExistingDocuments();
            setupDocumentClick();
            setupPageClick();
            updateDocumentListView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupRotationControls();
    }

    // ⭐ Rotation logic (slider + text field)
    private void setupRotationControls() {

        // Slider → rotates image + updates text field
        rotationSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double angle = newVal.doubleValue();
            imagePreview.setRotate(angle);
            rotationField.setText(String.valueOf((int) angle));
        });

        // Text field → rotates image + updates slider
        rotationField.setOnAction(e -> applyTextRotation());

        // Apply rotation when leaving the field
        rotationField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) applyTextRotation();
        });
    }

    private void applyTextRotation() {
        try {
            double angle = Double.parseDouble(rotationField.getText());
            if (angle < 0 || angle > 360) return;

            imagePreview.setRotate(angle);
            rotationSlider.setValue(angle);

        } catch (NumberFormatException ignored) {
            // Ignore invalid input
        }
    }

    private void loadExistingDocuments() throws Exception {
        Box box = scanManager.getCurrentBox();
        List<Document> docs = docRepo.getDocumentsByBoxId(box.getId());

        for (Document d : docs) {
            List<ScannedFile> pages = pageRepo.getPagesByDocumentId(d.getId());
            pages.forEach(d::addPage);
            box.addDocument(d);
        }
    }

    private void updateDocumentListView() {
        documentListView.getItems().clear();

        List<Document> docs = scanManager.getAllDocuments();
        for (int i = 0; i < docs.size(); i++) {
            documentListView.getItems().add("Document " + (i + 1));
        }

        docCountLabel.setText(String.valueOf(docs.size()));

        if (!docs.isEmpty()) {
            documentListView.getSelectionModel().select(docs.size() - 1);
            updatePageList();
        }
    }

    private void updatePageList() {
        pageListView.getItems().clear();

        int docIndex = documentListView.getSelectionModel().getSelectedIndex();
        if (docIndex < 0) return;

        Document doc = scanManager.getAllDocuments().get(docIndex);

        for (int i = 0; i < doc.getPages().size(); i++) {
            pageListView.getItems().add("Page " + (i + 1));
        }
    }

    private void setupDocumentClick() {
        documentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updatePageList());
    }

    private void setupPageClick() {
        pageListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            int docIndex = documentListView.getSelectionModel().getSelectedIndex();
            int pageIndex = pageListView.getSelectionModel().getSelectedIndex();
            if (docIndex < 0 || pageIndex < 0) return;

            ScannedFile page = scanManager.getAllDocuments().get(docIndex).getPages().get(pageIndex);
            imagePreview.setImage(imageService.toFxImage(page.getImage()));

            // Reset rotation when switching pages
            rotationSlider.setValue(0);
        });
    }

    @FXML
    private void onScan() {
        try {
            List<ScannedFile> newFiles = scanManager.scanNext();
            if (newFiles.isEmpty()) return;

            ScannedFile last = newFiles.get(newFiles.size() - 1);
            imagePreview.setImage(imageService.toFxImage(last.getImage()));

            fileCountLabel.setText(String.valueOf(scanManager.getTotalFileCount()));
            sessionCountLabel.setText("Scanned this session: " + scanManager.getSessionScanCount());
            statusLabel.setText(last.hasBarcode() ? "BARCODE: " + last.getBarcode() : "Scanned");

            updateDocumentListView();

            rotationSlider.setValue(0);

        } catch (Exception e) {
            statusLabel.setText("Error scanning.");
            e.printStackTrace();
        }
    }

    // PAGE REORDERING

    @FXML
    private void onMovePageUp() {
        int docIndex = documentListView.getSelectionModel().getSelectedIndex();
        int pageIndex = pageListView.getSelectionModel().getSelectedIndex();
        if (docIndex < 0 || pageIndex <= 0) return;

        Document doc = scanManager.getAllDocuments().get(docIndex);

        ScannedFile page = doc.getPages().remove(pageIndex);
        doc.getPages().add(pageIndex - 1, page);

        savePageOrderToDatabase(doc);
        updatePageList();
        pageListView.getSelectionModel().select(pageIndex - 1);
    }

    @FXML
    private void onMovePageDown() {
        int docIndex = documentListView.getSelectionModel().getSelectedIndex();
        int pageIndex = pageListView.getSelectionModel().getSelectedIndex();
        if (docIndex < 0) return;

        Document doc = scanManager.getAllDocuments().get(docIndex);
        if (pageIndex < 0 || pageIndex >= doc.getPages().size() - 1) return;

        ScannedFile page = doc.getPages().remove(pageIndex);
        doc.getPages().add(pageIndex + 1, page);

        savePageOrderToDatabase(doc);
        updatePageList();
        pageListView.getSelectionModel().select(pageIndex + 1);
    }

    private void savePageOrderToDatabase(Document doc) {
        try {
            for (int i = 0; i < doc.getPages().size(); i++) {
                ScannedFile page = doc.getPages().get(i);
                pageRepo.updatePageNumber(doc.getId(), i + 1, page.getFilePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onReset() {
        scanManager.reset();
        documentListView.getItems().clear();
        pageListView.getItems().clear();
        imagePreview.setImage(null);
        loadStats();
        statusLabel.setText("Session reset.");
        rotationSlider.setValue(0);
    }

    private void loadUserInfo(User user) {
        welcomeLabel.setText("Welcome back, " + user.getUsername());
        activeProfileLabel.setText(user.getUsername());
    }

    private void loadStats() {
        docCountLabel.setText("0");
        fileCountLabel.setText("0");
        sessionCountLabel.setText("Scanned this session: 0");
    }

    @FXML
    private void onLogout() {
        SceneController.setCurrentUser(null);
        SceneController.clearHistory();
        SceneController.switchTo("Login.fxml");
    }

    @FXML
    private void onDashboard() {
        loadUserInfo(SceneController.getCurrentUser());
        loadStats();
    }
}
