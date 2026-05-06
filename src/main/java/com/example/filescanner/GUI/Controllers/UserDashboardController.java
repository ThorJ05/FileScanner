package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Profile;
import com.example.filescanner.BEE.ScannedFile;
import com.example.filescanner.BEE.User;
import com.example.filescanner.BLL.ProfileManager;
import com.example.filescanner.BLL.ScanManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label sessionCountLabel;
    @FXML private Label apiCountLabel;
    @FXML private Label statusLabel;
    @FXML private Label selectedProfileLabel;
    @FXML private Button scanButton;
    @FXML private ListView<String> fileListView;
    @FXML private ImageView imagePreview;
    @FXML private ComboBox<Profile> profileComboBox;

    private final ScanManager scanManager       = new ScanManager();
    private final ProfileManager profileManager = new ProfileManager();
    private final ObservableList<String> fileLabels = FXCollections.observableArrayList();

    private Profile selectedProfile;

    @FXML
    public void initialize() {
        loadUserInfo();
        loadApiCount();
        loadUserProfiles();

        fileListView.setItems(fileLabels);

        // Disable scan button until profile is selected
        scanButton.setDisable(true);

        // Profile selection listener
        profileComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedProfile = newVal;
                        selectedProfileLabel.setText("Active profile: " + newVal.getName());
                        scanButton.setDisable(false);
                    } else {
                        selectedProfile = null;
                        selectedProfileLabel.setText("No profile selected");
                        scanButton.setDisable(true);
                    }
                }
        );

        // File list click → show preview
        fileListView.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldIdx, newIdx) -> {
                    int idx = newIdx.intValue();
                    if (idx >= 0 && idx < scanManager.getScannedFiles().size()) {
                        showPreview(scanManager.getScannedFiles().get(idx));
                    }
                }
        );
    }

    private void loadUserInfo() {
        User user = SceneController.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome back, " + user.getUsername() + "!");
        } else {
            welcomeLabel.setText("Welcome back!");
        }
    }

    private void loadUserProfiles() {
        User user = SceneController.getCurrentUser();
        if (user == null) return;

        new Thread(() -> {
            try {
                // Parse userId from the user's id string
                int userId = Integer.parseInt(user.getId());
                List<Profile> profiles = profileManager.getProfilesForUser(userId);

                Platform.runLater(() -> {
                    profileComboBox.setItems(FXCollections.observableArrayList(profiles));

                    if (profiles.isEmpty()) {
                        statusLabel.setText("No profiles assigned. Contact your admin.");
                    } else {
                        statusLabel.setText("Select a profile to start scanning.");
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() ->
                        statusLabel.setText("Could not load profiles: " + e.getMessage())
                );
                e.printStackTrace();
            }
        }).start();
    }

    private void loadApiCount() {
        new Thread(() -> {
            try {
                int count = scanManager.getApiCount();
                Platform.runLater(() ->
                        apiCountLabel.setText("Available files in API: " + count)
                );
            } catch (Exception e) {
                Platform.runLater(() ->
                        apiCountLabel.setText("Could not reach API")
                );
            }
        }).start();
    }

    @FXML
    private void onScan() {
        if (selectedProfile == null) {
            statusLabel.setText("Please select a profile first.");
            return;
        }

        scanButton.setDisable(true);
        statusLabel.setText("Scanning...");

        new Thread(() -> {
            try {
                List<ScannedFile> newFiles = scanManager.scanNext();

                Platform.runLater(() -> {
                    for (ScannedFile file : newFiles) {
                        fileLabels.add(file.getLabel());
                    }

                    ScannedFile last = newFiles.get(newFiles.size() - 1);
                    Image fxImage = SwingFXUtils.toFXImage(last.getImage(), null);
                    imagePreview.setImage(fxImage);
                    fileListView.getSelectionModel().selectLast();

                    sessionCountLabel.setText(
                            "Scanned this session: " + scanManager.getSessionScanCount()
                    );
                    statusLabel.setText("Scanned " + newFiles.size()
                            + " file(s). Last: " + last.getLabel());
                    scanButton.setDisable(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Scan failed: " + e.getMessage());
                    scanButton.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onReset() {
        scanManager.reset();
        fileLabels.clear();
        imagePreview.setImage(null);
        sessionCountLabel.setText("Scanned this session: 0");
        statusLabel.setText("Session reset. Select a profile to start scanning.");
        scanButton.setDisable(selectedProfile == null);
    }

    private void showPreview(ScannedFile file) {
        Image fxImage = SwingFXUtils.toFXImage(file.getImage(), null);
        imagePreview.setImage(fxImage);
    }

    @FXML
    private void onDashboard() {
        loadUserInfo();
    }

    @FXML
    private void onLogout() {
        scanManager.reset();
        SceneController.setCurrentUser(null);
        SceneController.clearHistory();
        SceneController.switchTo("Login.fxml");
    }
}