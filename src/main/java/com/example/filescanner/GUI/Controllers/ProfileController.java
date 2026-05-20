package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Profile;
import com.example.filescanner.BLL.ProfileManager;
import com.example.filescanner.DAL.ProfileRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class ProfileController {

    // TEXT FIELDS
    @FXML private TextField txtName;
    @FXML private TextField txtRotationValue;
    @FXML private TextField txtBrightnessValue;
    @FXML private TextField txtContrastValue;

    // SLIDERS
    @FXML private Slider sliderRotation;
    @FXML private Slider sliderBrightness;
    @FXML private Slider sliderContrast;

    // COMBOBOX
    @FXML private ComboBox<String> comboFormat;

    // CHECKBOXES
    @FXML private CheckBox chkSplitOnBarcode;
    @FXML private CheckBox chkAutoCrop;

    // PREVIEW
    @FXML private ImageView imgPreview;

    // ACTIVE TABLE
    @FXML private TableView<Profile> tblProfiles;
    @FXML private TableColumn<Profile, Integer> colId;
    @FXML private TableColumn<Profile, String> colName;

    // DELETED TABLE
    @FXML private TableView<Profile> tblDeletedProfiles;
    @FXML private TableColumn<Profile, Integer> colDeletedId;
    @FXML private TableColumn<Profile, String> colDeletedName;

    private ProfileManager profileManager;
    private Profile selectedProfile;
    private String currentPreviewPath;

    @FXML
    public void initialize() {
        profileManager = new ProfileManager(new ProfileRepository());
        setupColumns();
        setupSliders();
        setupFormatCombo();
        setupContextMenus();
        loadAllData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        colDeletedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDeletedName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tblProfiles.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) loadProfileIntoEditor(newV);
        });
    }

    private void setupSliders() {
        sliderRotation.valueProperty().addListener((obs, oldV, newV) -> {
            txtRotationValue.setText(String.valueOf(newV.intValue()));
            updatePreview();
        });

        sliderBrightness.valueProperty().addListener((obs, oldV, newV) -> {
            txtBrightnessValue.setText(String.valueOf(newV.floatValue()));
            updatePreview();
        });

        sliderContrast.valueProperty().addListener((obs, oldV, newV) -> {
            txtContrastValue.setText(String.valueOf(newV.floatValue()));
            updatePreview();
        });
    }

    private void setupFormatCombo() {
        comboFormat.getItems().addAll("TIFF", "PNG", "JPG");
    }

    private void loadAllData() {
        tblProfiles.getItems().setAll(profileManager.getAllProfiles());
        tblDeletedProfiles.getItems().setAll(profileManager.getDeletedProfiles());
    }

    private void loadProfileIntoEditor(Profile p) {
        selectedProfile = p;

        txtName.setText(p.getName());
        sliderRotation.setValue(p.getRotation());
        sliderBrightness.setValue(p.getBrightness());
        sliderContrast.setValue(p.getContrast());

        txtRotationValue.setText(String.valueOf(p.getRotation()));
        txtBrightnessValue.setText(String.valueOf(p.getBrightness()));
        txtContrastValue.setText(String.valueOf(p.getContrast()));

        chkSplitOnBarcode.setSelected(p.isSplitOnBarcode());
        chkAutoCrop.setSelected(p.isAutoCrop());

        comboFormat.setValue(p.getExportFormat());
    }

    @FXML
    private void createProfile() {
        try {
            Profile p = new Profile(
                    txtName.getText(),
                    Integer.parseInt(txtRotationValue.getText()),
                    Float.parseFloat(txtBrightnessValue.getText()),
                    Float.parseFloat(txtContrastValue.getText()),
                    chkSplitOnBarcode.isSelected(),
                    comboFormat.getValue()
            );

            p.setAutoCrop(chkAutoCrop.isSelected());

            profileManager.createProfile(p);
            loadAllData();
            clearEditor();

        } catch (Exception e) {
            showError("Invalid profile settings: " + e.getMessage());
        }
    }

    @FXML
    private void saveProfile() {
        if (selectedProfile == null) {
            showError("No profile selected");
            return;
        }

        try {
            selectedProfile.setName(txtName.getText());
            selectedProfile.setRotation(Integer.parseInt(txtRotationValue.getText()));
            selectedProfile.setBrightness(Float.parseFloat(txtBrightnessValue.getText()));
            selectedProfile.setContrast(Float.parseFloat(txtContrastValue.getText()));
            selectedProfile.setSplitOnBarcode(chkSplitOnBarcode.isSelected());
            selectedProfile.setAutoCrop(chkAutoCrop.isSelected());
            selectedProfile.setExportFormat(comboFormat.getValue());

            profileManager.updateProfile(selectedProfile);
            loadAllData();

        } catch (Exception e) {
            showError("Invalid profile settings: " + e.getMessage());
        }
    }

    @FXML
    private void deleteProfile() {
        Profile p = tblProfiles.getSelectionModel().getSelectedItem();
        if (p == null) return;

        profileManager.deleteProfile(p.getId());
        loadAllData();
        clearEditor();
    }

    @FXML
    private void restoreProfile() {
        Profile p = tblDeletedProfiles.getSelectionModel().getSelectedItem();
        if (p == null) return;

        profileManager.restoreProfile(p.getId());
        loadAllData();
    }

    private void setupContextMenus() {
        ContextMenu activeMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Soft Delete");
        deleteItem.setOnAction(e -> deleteProfile());
        activeMenu.getItems().add(deleteItem);
        tblProfiles.setContextMenu(activeMenu);

        ContextMenu deletedMenu = new ContextMenu();
        MenuItem restoreItem = new MenuItem("Restore");
        restoreItem.setOnAction(e -> restoreProfile());
        deletedMenu.getItems().add(restoreItem);
        tblDeletedProfiles.setContextMenu(deletedMenu);
    }

    private void clearEditor() {
        txtName.clear();
        txtRotationValue.clear();
        txtBrightnessValue.clear();
        txtContrastValue.clear();
        comboFormat.setValue(null);
        chkSplitOnBarcode.setSelected(false);
        chkAutoCrop.setSelected(false);
        selectedProfile = null;
    }

    @FXML
    private void loadSampleImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select sample image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.png", "*.jpg", "*.jpeg", "*.tif", "*.tiff"
                )
        );

        File file = chooser.showOpenDialog(null);

        if (file != null) {
            currentPreviewPath = file.getAbsolutePath();
            updatePreview();
        }
    }

    private void updatePreview() {
        if (currentPreviewPath == null) return;

        try {
            Image img = new Image(new File(currentPreviewPath).toURI().toString());

            imgPreview.setRotate(sliderRotation.getValue());

            javafx.scene.effect.ColorAdjust adjust = new javafx.scene.effect.ColorAdjust();
            adjust.setBrightness(sliderBrightness.getValue());
            adjust.setContrast(sliderContrast.getValue());

            imgPreview.setEffect(adjust);
            imgPreview.setImage(img);

        } catch (Exception e) {
            showError("Preview error: " + e.getMessage());
        }
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
