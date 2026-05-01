package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.Profile;
import com.example.filescanner.BLL.ImageProcessor;
import com.example.filescanner.BLL.ProfileManager;
import com.example.filescanner.DAL.ProfileRepository;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;

import com.example.filescanner.Util.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.File;

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
    @FXML private TextField txtRotationValue;
    @FXML private TextField txtBrightnessValue;
    @FXML private TextField txtContrastValue;
    @FXML private CheckBox chkSplit;
    @FXML private ComboBox<String> comboFormat;
    @FXML private ImageView imgPreview;
    @FXML private Button btnLoadSample;

    private final ProfileManager manager = new ProfileManager(new ProfileRepository());
    private final ImageProcessor imageProcessor = new ImageProcessor();
    private BufferedImage sampleImage = null;
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

        // Keep text fields and sliders in sync and update preview when changed

        sliderRotation.valueProperty().addListener((obs, old, val) -> {
            int deg = (int) Math.round(val.doubleValue());
            if (txtRotationValue != null && !txtRotationValue.isFocused()) {
                txtRotationValue.setText(String.valueOf(deg));
            }
            updatePreview();
        });

        if (txtRotationValue != null) {
            txtRotationValue.setOnAction(e -> {
                try {
                    int v = Integer.parseInt(txtRotationValue.getText().trim());
                    if (v < 0) v = 0; if (v > 360) v = 360;
                    sliderRotation.setValue(v);
                } catch (Exception ex) {
                    // ignore invalid input
                    txtRotationValue.setText(String.format("%d", (int) sliderRotation.getValue()));
                }
                updatePreview();
            });
            txtRotationValue.focusedProperty().addListener((obs, oldF, focused) -> { if (!focused) { try { int v = Integer.parseInt(txtRotationValue.getText().trim()); if (v < 0) v = 0; if (v > 360) v = 360; sliderRotation.setValue(v); } catch (Exception ex) { txtRotationValue.setText(String.format("%d", (int) sliderRotation.getValue())); } updatePreview(); } });
        }

        sliderBrightness.valueProperty().addListener((obs, old, val) -> {
            if (txtBrightnessValue != null && !txtBrightnessValue.isFocused())
                txtBrightnessValue.setText(String.format("%.0f", val.doubleValue()));
            updatePreview();
        });

        sliderContrast.valueProperty().addListener((obs, old, val) -> {
            if (txtContrastValue != null && !txtContrastValue.isFocused())
                txtContrastValue.setText(String.format("%.2f", val.doubleValue()));
            updatePreview();
        });

        if (txtBrightnessValue != null) {
            txtBrightnessValue.setOnAction(e -> {
                try { double v = Double.parseDouble(txtBrightnessValue.getText()); sliderBrightness.setValue(v); } catch (Exception ex) {}
                updatePreview();
            });
            txtBrightnessValue.focusedProperty().addListener((obs, oldF, focused) -> { if (!focused) { try { double v = Double.parseDouble(txtBrightnessValue.getText()); sliderBrightness.setValue(v); } catch (Exception ex) {} updatePreview(); } });
        }

        if (txtContrastValue != null) {
            txtContrastValue.setOnAction(e -> {
                try { double v = Double.parseDouble(txtContrastValue.getText()); sliderContrast.setValue(v); } catch (Exception ex) {}
                updatePreview();
            });
            txtContrastValue.focusedProperty().addListener((obs, oldF, focused) -> { if (!focused) { try { double v = Double.parseDouble(txtContrastValue.getText()); sliderContrast.setValue(v); } catch (Exception ex) {} updatePreview(); } });
        }

        if (btnLoadSample != null) {
            btnLoadSample.setOnAction(e -> loadSampleImage());
        }
    }

    private void loadProfiles() {
        profileTable.getItems().setAll(manager.getAllProfiles());
    }

    private void loadProfileIntoFields(Profile p) {
        selected = p;
        txtName.setText(p.getName());
        sliderRotation.setValue(p.getRotation());
        if (txtRotationValue != null) txtRotationValue.setText(String.valueOf(p.getRotation()));
        sliderBrightness.setValue(p.getBrightness());
        sliderContrast.setValue(p.getContrast());
        // update text fields as well
        if (txtBrightnessValue != null) txtBrightnessValue.setText(String.format("%.0f", p.getBrightness()));
        if (txtContrastValue != null) txtContrastValue.setText(String.format("%.2f", p.getContrast()));
        chkSplit.setSelected(p.isSplitOnBarcode());
        comboFormat.setValue(p.getExportFormat());

        // update preview with current profile if a sample image is loaded
        updatePreview();
    }

    @FXML
    public void createProfile() {
        if (txtName.getText().isEmpty()) return;

        int rotation = (int) Math.round(sliderRotation.getValue());

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

        int rotation = (int) Math.round(sliderRotation.getValue());

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
        if (txtRotationValue != null) txtRotationValue.setText("0");
        sliderBrightness.setValue(0);
        sliderContrast.setValue(1);
        if (txtBrightnessValue != null) txtBrightnessValue.setText("0");
        if (txtContrastValue != null) txtContrastValue.setText("1.00");
        chkSplit.setSelected(false);
        comboFormat.setValue(null);
        selected = null;
        sampleImage = null;
        if (imgPreview != null) imgPreview.setImage(null);
    }

    private void updatePreview() {
        if (sampleImage == null || imageProcessor == null) return;

        int rotation = (int) Math.round(sliderRotation.getValue());
        float b = (float) sliderBrightness.getValue();
        float c = (float) sliderContrast.getValue();

        BufferedImage processed = imageProcessor.applyProfile(sampleImage, rotation, b, c);
        if (processed != null && imgPreview != null) {
            Image fx = SwingFXUtils.toFXImage(processed, null);
            imgPreview.setImage(fx);
        }
    }

    private void loadSampleImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select sample image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.tif", "*.tiff")
        );
        File f = chooser.showOpenDialog(null);
        if (f != null) {
            try {
                BufferedImage img = ImageLoader.loadImage(f);
                if (img != null) {
                    sampleImage = img;
                    updatePreview();
                } else {
                    System.err.println("Unable to read image: " + f.getAbsolutePath());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

