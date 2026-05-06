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

    @FXML private ComboBox<Profile> comboProfiles;

    private ClientManager clientManager;
    private ProfileManager profileManager;

    @FXML
    public void initialize() {
        clientManager = new ClientManager(new ClientRepository());
        profileManager = new ProfileManager(new ProfileRepository());

        // Clients table
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("companyName"));

        // Assigned profiles column (simple, no lambdas)
        colAssignedProfiles.setCellValueFactory(cellData -> {
            Client client = cellData.getValue();
            List<Profile> profiles = profileManager.getProfilesForClient(client.getId());

            String names = "";
            for (Profile p : profiles) {
                if (!names.isEmpty()) {
                    names += ", ";
                }
                names += p.getName();
            }

            return new javafx.beans.property.SimpleStringProperty(names);
        });

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

        loadClients();
    }


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
