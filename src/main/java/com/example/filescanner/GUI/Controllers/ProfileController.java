package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Profile;
import com.example.filescanner.BLL.ProfileManager;
import com.example.filescanner.BLL.ProfileMapper;
import com.example.filescanner.BLL.PreviewService;
import com.example.filescanner.DAL.ProfileRepository;
import com.example.filescanner.Util.ImageLoader;
import com.example.filescanner.GUI.helpers.SliderBinder;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProfileController {

    @FXML private TableView<Profile> profileTable;
    @FXML private TableColumn<Profile, String> colName;
    @FXML private TableColumn<Profile, Integer> colRotation;
    @FXML private TableColumn<Profile, String> colFormat;
    @FXML private TableColumn<Profile, Float> colBrightness;
    @FXML private TableColumn<Profile, Float> colContrast;
    @FXML private TableColumn<Profile, Boolean> colSplit;

    @FXML private TextField txtName, txtRotationValue, txtBrightnessValue, txtContrastValue;
    @FXML private Slider sliderRotation, sliderBrightness, sliderContrast;
    @FXML private CheckBox chkSplit;
    @FXML private ComboBox<String> comboFormat;
    @FXML private ImageView imgPreview;

    private final ProfileManager manager = new ProfileManager(new ProfileRepository());

    private final PreviewService previewService = new PreviewService();

    private BufferedImage sampleImage;
    private Profile selected;

    @FXML
    public void initialize() {
        setupTable();
        loadProfiles();
        setupBindings();
    }

    private void setupTable() {
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colRotation.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getRotation()).asObject());
        colFormat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExportFormat()));
        colBrightness.setCellValueFactory(c -> new SimpleFloatProperty(c.getValue().getBrightness()).asObject());
        colContrast.setCellValueFactory(c -> new SimpleFloatProperty(c.getValue().getContrast()).asObject());
        colSplit.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isSplitOnBarcode()).asObject());

        profileTable.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) loadProfileIntoFields(p);
        });
    }

    private void setupBindings() {
        SliderBinder.bind(sliderRotation, txtRotationValue, this::updatePreview, 0, 360);
        SliderBinder.bind(sliderBrightness, txtBrightnessValue, this::updatePreview, -255, 255);
        SliderBinder.bind(sliderContrast, txtContrastValue, this::updatePreview, 0.1, 100);

        comboFormat.getItems().addAll("TIFF", "PNG", "JPG");
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
        updatePreview();
    }

    @FXML
    public void createProfile() {
        Profile p = ProfileMapper.fromFields(txtName, sliderRotation, sliderBrightness, sliderContrast, chkSplit, comboFormat);
        manager.createProfile(p);
        loadProfiles();
        clearFields();
    }

    @FXML
    public void saveProfile() {
        if (selected == null) return;

        ProfileMapper.updateProfile(selected, txtName, sliderRotation, sliderBrightness, sliderContrast, chkSplit, comboFormat);
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

    @FXML
    private void loadSampleImage() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select sample image");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TIFF Files", "*.tif", "*.tiff")
        );

        File f = chooser.showOpenDialog(null);
        if (f != null) {
            sampleImage = ImageLoader.loadTiff(f);
            updatePreview();
        }
    }


    private void updatePreview() {
        if (sampleImage == null) return;

        Profile p = selected;

        if (p == null) {
            p = new Profile(
                    txtName.getText(),
                    (int) sliderRotation.getValue(),
                    (float) sliderBrightness.getValue(),
                    (float) sliderContrast.getValue(),
                    chkSplit.isSelected(),
                    comboFormat.getValue()
            );
        }
        imgPreview.setImage(previewService.generatePreview(sampleImage, p));
    }


    private void clearFields() {
        txtName.clear();
        sliderRotation.setValue(0);
        sliderBrightness.setValue(0);
        sliderContrast.setValue(1);
        chkSplit.setSelected(false);
        comboFormat.setValue(null);
        imgPreview.setImage(null);
        selected = null;
        sampleImage = null;
    }
}
