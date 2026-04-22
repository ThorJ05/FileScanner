package com.example.filescanner.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserDashboardController {

    @FXML private Label docCountLabel;
    @FXML private Label fileCountLabel;
    @FXML private Label activeProfileLabel;

    @FXML
    public void initialize() {
        docCountLabel.setText("0");
        fileCountLabel.setText("0");
        activeProfileLabel.setText("None");
    }

    @FXML
    private void onDashboard() {}

    @FXML
    private void onLogout() {
        SceneController.switchTo("Login.fxml");
    }
}