package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;
import com.example.filescanner.BLL.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class AdminDashboardController {

    @FXML private Label userCountLabel;
    @FXML private ListView<String> userListView;
    @FXML private ListView<String> deletedUsersListView;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label statusLabel;
    @FXML private Button createUserButton;
    @FXML private AnchorPane contentArea;
    @FXML private TextField searchUserField;

    private final UserManager userManager = new UserManager();
    private List<User> currentUsers;
    private FilteredList<User> filteredUsers;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("USER", "ADMIN");
        roleComboBox.getSelectionModel().selectFirst();

        loadUsers();
        loadDeletedUsers();

        userListView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                setupKeyboardShortcuts(newScene);
                setupArrowNavigation();
            }
        });
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
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) passwordField.requestFocus();
        });

        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) usernameField.requestFocus();
            else if (e.getCode() == KeyCode.DOWN) roleComboBox.requestFocus();
        });

        roleComboBox.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> roleComboBox.show();
                case UP -> passwordField.requestFocus();
                case DOWN -> createUserButton.requestFocus();
            }
        });
    }

    // ⭐ Load active users
    private void loadUsers() {
        currentUsers = userManager.getAllUsers();
        userCountLabel.setText(String.valueOf(currentUsers.size()));

        filteredUsers = new FilteredList<>(FXCollections.observableArrayList(currentUsers), p -> true);

        refreshListView();
        setupSearchFilter();
    }

    private void refreshListView() {
        userListView.setItems(FXCollections.observableArrayList(
                filteredUsers.stream()
                        .map(u -> u.getUsername() + " (" + u.getRole() + ")")
                        .toList()
        ));
    }

    private void setupSearchFilter() {
        if (searchUserField == null) return;

        searchUserField.textProperty().addListener((obs, oldValue, newValue) -> {
            String filter = newValue.toLowerCase().trim();

            filteredUsers.setPredicate(user -> {
                if (filter.isEmpty()) return true;
                return user.getUsername().toLowerCase().contains(filter)
                        || user.getRole().toString().toLowerCase().contains(filter);
            });

            refreshListView();
        });
    }

    @FXML
    private void onCreateUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String roleStr  = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password are required.");
            return;
        }

        UserRole role = roleStr.equals("ADMIN") ? UserRole.ADMIN : UserRole.USER;

        userManager.createUser(username, password, role);

        statusLabel.setText("User created: " + username);
        clearFields();
        loadUsers();
        loadDeletedUsers();
    }

    @FXML
    private void onDeleteUser() {
        int selectedIndex = userListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            statusLabel.setText("Please select a user to delete.");
            return;
        }

        User selectedUser = filteredUsers.get(selectedIndex);

        userManager.deleteUser(selectedUser.getId()); // Soft delete

        statusLabel.setText("User soft-deleted.");
        loadUsers();
        loadDeletedUsers();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().selectFirst();
    }

    // ⭐ Load deleted users (IsDeleted = 1)
    private void loadDeletedUsers() {
        List<User> deleted = userManager.getDeletedUsers();

        deletedUsersListView.setItems(FXCollections.observableArrayList(
                deleted.stream()
                        .map(u -> u.getUsername() + " (" + u.getRole() + ")")
                        .toList()
        ));
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

    @FXML
    private void openProfiles() {
        SceneController.switchTo("profiles.fxml");
    }

    @FXML
    private void openClients() {
        SceneController.switchTo("clients.fxml");
    }

}
