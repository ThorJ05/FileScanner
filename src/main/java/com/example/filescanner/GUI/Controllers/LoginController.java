package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import com.example.filescanner.DAL.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    private void onLoginClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userRepository.login(username, password);

        if (user == null) {
            errorLabel.setText("Incorrect username or password");
            return;
        }

        SceneController.switchTo("UserDashboard.fxml");
    }
}