package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import com.example.filescanner.BEE.UserRole;
import com.example.filescanner.BLL.UserManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private final UserManager userManager = new UserManager();
    private List<User> currentUsers;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("USER", "ADMIN");
        roleComboBox.getSelectionModel().selectFirst();
        loadUsers();
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
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String company = companyField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String roleStr = roleComboBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        UserRole role = roleStr.equals("ADMIN") ? UserRole.ADMIN : UserRole.USER;
        userManager.createUser(firstName, lastName, email, password, role);

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
    private void onDashboard() {}

    @FXML
    private void onLogout() {
        SceneController.setCurrentUser(null);
        SceneController.switchTo("Login.fxml");
    }
}