package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import com.example.filescanner.BLL.LoginManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.Optional;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final LoginManager loginManager = new LoginManager();

    @FXML
    public void initialize() {
        setupArrowNavigation();
    }

    private void setupArrowNavigation() {
        usernameField.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN -> passwordField.requestFocus();
                case ENTER -> passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP -> usernameField.requestFocus();
                case ENTER -> onLoginClick();
            }
        });
    }


    @FXML
    private void onLoginClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        Optional<User> userOpt = loginManager.attemptLogin(username, password);

        if (userOpt.isEmpty()) {
            errorLabel.setText("Incorrect username or password");
            return;
        }

        User user = userOpt.get();
        SceneController.setCurrentUser(user);

        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            SceneController.switchTo("AdminDashboard.fxml");
        } else {
            SceneController.switchTo("UserDashboard.fxml");
        }
    }
}
