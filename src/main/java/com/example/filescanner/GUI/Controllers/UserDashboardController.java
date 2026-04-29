package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label docCountLabel;
    @FXML private Label fileCountLabel;
    @FXML private Label activeProfileLabel;

    @FXML
    public void initialize() {
        loadUserInfo();
        loadStats();
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
    private void onDashboard() {}

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
}