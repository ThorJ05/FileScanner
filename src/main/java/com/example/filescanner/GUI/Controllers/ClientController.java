package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Client;
import com.example.filescanner.BEE.Profile;
import com.example.filescanner.BLL.ClientManager;
import com.example.filescanner.BLL.ProfileManager;
import com.example.filescanner.DAL.ClientRepository;
import com.example.filescanner.DAL.ProfileRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ClientController {

    @FXML private TextField txtCompanyName;

    @FXML private TableView<Client> tblClients;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colCompany;
    @FXML private TableColumn<Client, String> colAssignedProfiles;

    @FXML private TableView<Client> tblDeletedClients;
    @FXML private TableColumn<Client, Integer> colDeletedId;
    @FXML private TableColumn<Client, String> colDeletedCompany;

    @FXML private ComboBox<Profile> comboProfiles;

    private ClientManager clientManager;
    private ProfileManager profileManager;

    @FXML
    public void initialize() {
        setupManagers();
        setupTableColumns();
        loadAllData();
        setupContextMenus();
    }

    private void setupManagers() {
        clientManager = new ClientManager(new ClientRepository());
        profileManager = new ProfileManager(new ProfileRepository());
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("companyName"));

        colAssignedProfiles.setCellValueFactory(cellData -> {
            Client client = cellData.getValue();
            List<Profile> profiles = profileManager.getProfilesForClient(client.getId());

            StringBuilder names = new StringBuilder();
            for (Profile p : profiles) {
                if (!names.isEmpty()) names.append(", ");
                names.append(p.getName());
            }
            return new javafx.beans.property.SimpleStringProperty(names.toString());
        });

        colDeletedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDeletedCompany.setCellValueFactory(new PropertyValueFactory<>("companyName"));
    }

    private void loadAllData() {
        loadClients();
        loadDeletedClients();
        loadProfiles();
    }

    private void loadClients() {
        tblClients.getItems().setAll(clientManager.getAllClients());
    }

    private void loadDeletedClients() {
        tblDeletedClients.getItems().setAll(clientManager.getDeletedClients());
    }

    private void loadProfiles() {
        comboProfiles.getItems().setAll(profileManager.getAllProfiles());
    }

    @FXML
    private void createClient() {
        try {
            Client c = new Client(0, txtCompanyName.getText());
            clientManager.createClient(c);
            loadClients();
            txtCompanyName.clear();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void assignProfile() {
        Client selectedClient = tblClients.getSelectionModel().getSelectedItem();
        Profile selectedProfile = comboProfiles.getSelectionModel().getSelectedItem();

        if (selectedClient == null || selectedProfile == null) {
            showError("Select both client and profile");
            return;
        }

        profileManager.assignProfileToClient(selectedClient.getId(), selectedProfile.getId());
        loadClients();
    }

    // Soft delete
    @FXML
    private void onSoftDeleteClient() {
        Client selected = tblClients.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        clientManager.deleteClient(selected.getId());
        loadClients();
        loadDeletedClients();
    }

    // Restore
    @FXML
    private void onRestoreClient() {
        Client selected = tblDeletedClients.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        clientManager.restoreClient(selected.getId());
        loadClients();
        loadDeletedClients();
    }

    private void setupContextMenus() {
        setupActiveContextMenu();
        setupDeletedContextMenu();
    }

    private void setupActiveContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Soft Delete");
        deleteItem.setOnAction(e -> onSoftDeleteClient());
        menu.getItems().add(deleteItem);
        tblClients.setContextMenu(menu);
    }

    private void setupDeletedContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem restoreItem = new MenuItem("Restore");
        restoreItem.setOnAction(e -> onRestoreClient());
        menu.getItems().add(restoreItem);
        tblDeletedClients.setContextMenu(menu);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void onBack() {
        SceneController.goBack();
    }

    @FXML
    private void openShortcuts() {
        SceneController.switchTo("Shortcuts.fxml");
    }
}
