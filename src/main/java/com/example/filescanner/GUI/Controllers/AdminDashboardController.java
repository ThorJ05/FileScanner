package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;
import com.example.filescanner.BLL.UserManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class AdminDashboardController {

    @FXML private Label userCountLabel;
    @FXML private ListView<String> userListView;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField companyField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label statusLabel;
    @FXML private Button createUserButton;
    @FXML
    private AnchorPane contentArea;


    private final UserManager userManager = new UserManager();
    private List<User> currentUsers;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("USER", "ADMIN");
        roleComboBox.getSelectionModel().selectFirst();
        loadUsers();

        // Wait until the Scene is attached before adding keyboard shortcuts
        userListView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                setupKeyboardShortcuts(newScene);
                setupArrowNavigation();
            }
        });
    }

    @FXML
    public void openProfiles() {
        SceneController.switchTo("profiles.fxml");
    }

    @FXML
    private void openClients() {
       SceneController.switchTo("clients.fxml");
    }


    private void setupKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE -> onLogout();
                case DELETE -> onDeleteUser();
                case DIGIT1 -> onDashboard();
            }
        });
    }

    private void setupArrowNavigation() {
        firstNameField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.DOWN) lastNameField.requestFocus(); });
        lastNameField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.UP) firstNameField.requestFocus(); else if (e.getCode() == KeyCode.DOWN) companyField.requestFocus(); });
        companyField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.UP) lastNameField.requestFocus(); else if (e.getCode() == KeyCode.DOWN) emailField.requestFocus(); });
        emailField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.UP) companyField.requestFocus(); else if (e.getCode() == KeyCode.DOWN) passwordField.requestFocus(); });
        passwordField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.UP) emailField.requestFocus(); else if (e.getCode() == KeyCode.DOWN) roleComboBox.requestFocus(); });

        roleComboBox.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> roleComboBox.show();
                case UP -> passwordField.requestFocus();
                case DOWN -> createUserButton.requestFocus();
            }
        });
    }

    private void loadUsers() {
        currentUsers = userManager.getAllUsers();
        userCountLabel.setText(String.valueOf(currentUsers.size()));

        userListView.setItems(FXCollections.observableArrayList(
                currentUsers.stream()
                        .map(u -> u.getFirstName() + " " + u.getLastName() + " (" + u.getRole() + ")")
                        .toList()
        ));
    }

    @FXML
    private void onCreateUser() {
        String username = firstNameField.getText().trim();  // username
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String roleStr = roleComboBox.getValue();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        UserRole role = roleStr.equals("ADMIN") ? UserRole.ADMIN : UserRole.USER;

        userManager.createUser(username, firstName, lastName, email, password, role);

        statusLabel.setText("User created successfully.");
        clearFields();
        loadUsers();
    }


    @FXML
    private void onDeleteUser() {
        int selectedIndex = userListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            statusLabel.setText("Please select a user to delete.");
            return;
        }

        User selectedUser = currentUsers.get(selectedIndex);
        userManager.deleteUser(selectedUser.getId());

        statusLabel.setText("User deleted successfully.");
        loadUsers();
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        companyField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void onDashboard() {
        // Future dashboard navigation
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


}