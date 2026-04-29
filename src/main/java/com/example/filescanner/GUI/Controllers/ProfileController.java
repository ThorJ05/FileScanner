package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Profile;
import com.example.filescanner.BLL.ProfileManager;
import com.example.filescanner.DAL.ProfileRepository;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProfileController {

    @FXML private TableView<Profile> profileTable;
    @FXML private TableColumn<Profile, String> colName;
    @FXML private TableColumn<Profile, Integer> colRotation;
    @FXML private TableColumn<Profile, String> colFormat;
    @FXML private TableColumn<Profile, Float> colBrightness;
    @FXML private TableColumn<Profile, Float> colContrast;
    @FXML private TableColumn<Profile, Boolean> colSplit;

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

        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colRotation.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getRotation()).asObject());
        colFormat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExportFormat()));
        colBrightness.setCellValueFactory(c -> new SimpleFloatProperty(c.getValue().getBrightness()).asObject());
        colContrast.setCellValueFactory(c -> new SimpleFloatProperty(c.getValue().getContrast()).asObject());
        colSplit.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isSplitOnBarcode()).asObject());

        comboFormat.getItems().addAll("PNG", "JPG", "TIFF");

        loadProfiles();

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
        if (txtName.getText().isEmpty()) return;

        // Tving rotation til 0/90/180/270
        int rotation = ((int) sliderRotation.getValue() / 90) * 90;

        Profile p = new Profile();
        p.setName(txtName.getText());
        p.setRotation(rotation);
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

        int rotation = ((int) sliderRotation.getValue() / 90) * 90;

        selected.setName(txtName.getText());
        selected.setRotation(rotation);
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

    @FXML
    private void onBack() {
        SceneController.goBack();
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
