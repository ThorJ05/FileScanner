package com.example.filescanner.BLL;

import com.example.filescanner.BEE.Profile;
import javafx.scene.control.*;

public class ProfileMapper {

    public static Profile fromFields(TextField name, Slider rot, Slider bright, Slider cont, CheckBox split, ComboBox<String> format) {
        return new Profile(
                name.getText(),
                (int) rot.getValue(),
                (float) bright.getValue(),
                (float) cont.getValue(),
                split.isSelected(),
                format.getValue()
        );
    }

    public static void updateProfile(Profile p, TextField name, Slider rot, Slider bright, Slider cont, CheckBox split, ComboBox<String> format) {
        p.setName(name.getText());
        p.setRotation((int) rot.getValue());
        p.setBrightness((float) bright.getValue());
        p.setContrast((float) cont.getValue());
        p.setSplitOnBarcode(split.isSelected());
        p.setExportFormat(format.getValue());
    }
}
