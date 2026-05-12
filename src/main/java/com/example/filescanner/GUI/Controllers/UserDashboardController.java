package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Box;
import com.example.filescanner.BEE.Document;
import com.example.filescanner.BEE.ScannedFile;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BLL.ImageService;
import com.example.filescanner.BLL.ScanManager;
import com.example.filescanner.DAL.DocumentRepository;
import com.example.filescanner.DAL.PageRepository;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    //  NEW: TableView for pages
    @FXML private TableView<ScannedFile> pageTable;
    @FXML private TableColumn<ScannedFile, Integer> colReferenceId;
    @FXML private TableColumn<ScannedFile, String> colLabel;
    @FXML private TableColumn<ScannedFile, String> colBarcode;

    //  Rotation controls
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
        setupTableColumns();
    }

    // Bind TableView columns
    private void setupTableColumns() {
        colReferenceId.setCellValueFactory(new PropertyValueFactory<>("referenceId"));
        colLabel.setCellValueFactory(new PropertyValueFactory<>("label"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
    }

    //  Rotation logic
    private void setupRotationControls() {

        rotationSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double angle = newVal.doubleValue();
            imagePreview.setRotate(angle);
            rotationField.setText(String.valueOf((int) angle));
        });

        rotationField.setOnAction(e -> applyTextRotation());

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

        } catch (NumberFormatException ignored) {}
    }

    private void loadExistingDocuments() throws Exception {
        Box box = scanManager.getCurrentBox();
        List<Document> docs = docRepo.getDocumentsByBoxId(box.getId());

        for (Document d : docs) {
            d.setPagesLoaded(false);
            box.addDocument(d);
        }

        if (!docs.isEmpty()) {
            scanManager.setCurrentDocument(docs.get(docs.size() - 1));
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

        // Old ListView
        for (int i = 0; i < doc.getPages().size(); i++) {
            pageListView.getItems().add("Page " + (i + 1));
        }

        //NEW: Update TableView
        pageTable.getItems().setAll(doc.getPages());
    }

    private void setupDocumentClick() {
        documentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {

            int docIndex = documentListView.getSelectionModel().getSelectedIndex();
            if (docIndex < 0) return;

            Document doc = scanManager.getAllDocuments().get(docIndex);


            if (!doc.isPagesLoaded()) {
                try {
                    List<ScannedFile> pages = pageRepo.getPagesByDocumentId(doc.getId());
                    pages.forEach(doc::addPage);
                    doc.setPagesLoaded(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            updatePageList();
        });
    }


    private void setupPageClick() {
        pageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            try {
                //
                if (newVal.getImage() == null) {
                    byte[] bytes = pageRepo.getImageBytes(newVal.getPageId());
                    if (bytes != null) {
                        newVal.setImage(imageService.decodeImage(bytes));
                    }
                }

                imagePreview.setImage(imageService.toFxImage(newVal.getImage()));
                rotationSlider.setValue(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @FXML
    private void onScan() {
        statusLabel.setText("Scanning...");

        Task<ScannedFile> task = new Task<>() {
            @Override
            protected ScannedFile call() throws Exception {
                return scanManager.scanNextAsync(); // NY metode
            }
        };

        task.setOnSucceeded(e -> {
            ScannedFile scanned = task.getValue();

            // Vis billedet i GUI med det samme
            imagePreview.setImage(imageService.toFxImage(scanned.getImage()));

            // Opdater GUI
            updateDocumentListView();
            rotationSlider.setValue(0);

            statusLabel.setText("Scanned");
        });

        task.setOnFailed(e -> {
            statusLabel.setText("Scan failed");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }


    // PAGE REORDERING
    @FXML
    private void onMovePageUp() {
        int docIndex = documentListView.getSelectionModel().getSelectedIndex();
        int pageIndex = pageTable.getSelectionModel().getSelectedIndex();
        if (docIndex < 0 || pageIndex <= 0) return;

        Document doc = scanManager.getAllDocuments().get(docIndex);

        ScannedFile page = doc.getPages().remove(pageIndex);
        doc.getPages().add(pageIndex - 1, page);

        savePageOrderToDatabase(doc);
        updatePageList();
        pageTable.getSelectionModel().select(pageIndex - 1);
    }

    @FXML
    private void onMovePageDown() {
        int docIndex = documentListView.getSelectionModel().getSelectedIndex();
        int pageIndex = pageTable.getSelectionModel().getSelectedIndex();
        if (docIndex < 0) return;

        Document doc = scanManager.getAllDocuments().get(docIndex);
        if (pageIndex < 0 || pageIndex >= doc.getPages().size() - 1) return;

        ScannedFile page = doc.getPages().remove(pageIndex);
        doc.getPages().add(pageIndex + 1, page);

        savePageOrderToDatabase(doc);
        updatePageList();
        pageTable.getSelectionModel().select(pageIndex + 1);
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
        try {
            scanManager.reset(Integer.parseInt(SceneController.getCurrentUser().getId()));

            // GUI reset
            documentListView.getItems().clear();
            pageTable.getItems().clear();
            imagePreview.setImage(null);

            docCountLabel.setText("0");
            fileCountLabel.setText("0");
            sessionCountLabel.setText("Scanned this session: 0");

            statusLabel.setText("New box created.");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
