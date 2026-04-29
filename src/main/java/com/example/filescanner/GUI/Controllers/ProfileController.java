package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Profile;
import com.example.filescanner.BLL.ProfileManager;
import com.example.filescanner.DAL.ProfileRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProfileController {

    @FXML private TableView<Profile> profileTable;
    @FXML private TableColumn<Profile, String> colName;
    @FXML private TableColumn<Profile, Integer> colRotation;
    @FXML private TableColumn<Profile, String> colFormat;

    @FXML private TextField txtName;
    @FXML private Slider sliderRotation;
    @FXML private Slider sliderBrightness;
    @FXML private Slider sliderContrast;
    @FXML private CheckBox chkSplit;
    @FXML private ComboBox<String> comboFormat;

    private final ProfileManager manager = new ProfileManager(new ProfileRepository());
    private Profile selected;

    @FXML
    public void initialize() {

        // TableView bindings
        colName.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));

        colRotation.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getRotation()).asObject());

        colFormat.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getExportFormat()));

        // ComboBox values
        comboFormat.getItems().addAll("PNG", "JPG", "TIFF");

        // Load profiles
        loadProfiles();

        // When selecting a profile in the table
        profileTable.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) loadProfileIntoFields(p);
        });
    }

    private void loadProfiles() {
        profileTable.getItems().setAll(manager.getAllProfiles());
    }

    private void loadProfileIntoFields(Profile p) {
        selected = p;
        txtName.setText(p.getName());
        sliderRotation.setValue(p.getRotation());
        sliderBrightness.setValue(p.getBrightness());
        sliderContrast.setValue(p.getContrast());
        chkSplit.setSelected(p.isSplitOnBarcode());
        comboFormat.setValue(p.getExportFormat());
    }

    @FXML
    public void createProfile() {
        Profile p = new Profile();
        p.setName(txtName.getText());
        p.setRotation((int) sliderRotation.getValue());
        p.setBrightness((float) sliderBrightness.getValue());
        p.setContrast((float) sliderContrast.getValue());
        p.setSplitOnBarcode(chkSplit.isSelected());
        p.setExportFormat(comboFormat.getValue());

        manager.createProfile(p);
        loadProfiles();
        clearFields();
    }

    @FXML
    public void saveProfile() {
        if (selected == null) return;

        selected.setName(txtName.getText());
        selected.setRotation((int) sliderRotation.getValue());
        selected.setBrightness((float) sliderBrightness.getValue());
        selected.setContrast((float) sliderContrast.getValue());
        selected.setSplitOnBarcode(chkSplit.isSelected());
        selected.setExportFormat(comboFormat.getValue());

        manager.updateProfile(selected);
        loadProfiles();
    }

    @FXML
    public void deleteProfile() {
        if (selected == null) return;

        manager.deleteProfile(selected.getId());
        loadProfiles();
        clearFields();
    }

    private void clearFields() {
        txtName.clear();
        sliderRotation.setValue(0);
        sliderBrightness.setValue(0);
        sliderContrast.setValue(1);
        chkSplit.setSelected(false);
        comboFormat.setValue(null);
        selected = null;
    }
}
