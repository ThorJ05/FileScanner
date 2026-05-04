package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Client;
import com.example.filescanner.BEE.Profile;
import com.example.filescanner.BLL.ClientManager;
import com.example.filescanner.BLL.ProfileManager;
import com.example.filescanner.DAL.ClientRepository;
import com.example.filescanner.DAL.ProfileRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClientController {

    @FXML private TextField txtCompanyName;
    @FXML private TableView<Client> tblClients;
    @FXML private TableView<Profile> tblClientProfiles;
    @FXML private ComboBox<Profile> comboProfiles;

    private ClientManager clientManager;
    private ProfileManager profileManager;

    @FXML
    public void initialize() {
        profileManager = new ProfileManager(new ProfileRepository());
        clientManager = new ClientManager(new ClientRepository());


        loadClients();
        loadProfiles();
    }

    private void loadClients() {
        tblClients.getItems().setAll(clientManager.getAllClients());
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
        loadClientProfiles(selectedClient.getId());
    }

    private void loadClientProfiles(int clientId) {
        tblClientProfiles.getItems().setAll(profileManager.getProfilesForClient(clientId));
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
