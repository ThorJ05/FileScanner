package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import com.example.filescanner.BLL.LoginManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final LoginManager loginManager = new LoginManager();

    @FXML
    private void onLoginClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        User user = loginManager.attemptLogin(username, password);

        if (user == null) {
            errorLabel.setText("Incorrect username or password");
            return;
        }

        SceneController.setCurrentUser(user);

        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            SceneController.switchTo("AdminDashboard.fxml");
        } else {
            SceneController.switchTo("UserDashboard.fxml");
        }
    }
}