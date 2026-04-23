package com.example.filescanner.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML private Label userCountLabel;
    @FXML private Label profileCountLabel;

    @FXML
    public void initialize() {
        userCountLabel.setText("0");
        profileCountLabel.setText("0");
    }

    @FXML
    private void onDashboard() {
        // Already on dashboard
    }

    @FXML
    private void onUsers() {
        // TODO: navigate to user management view
    } //yes

    @FXML
    private void onProfiles() {
        // TODO: navigate to profiles view
    }

    @FXML
    private void onLogs() {
        // TODO: navigate to logs view
    }

    @FXML
    private void onLogout() {
        SceneController.switchTo("Login.fxml");
    }
}