package com.example.filescanner.BLL;

import com.example.filescanner.BEE.Profile;
import javafx.scene.control.*;

public class ProfileMapper {

    public static Profile fromFields(TextField name, Slider rot, Slider b, Slider c,
                                     CheckBox split, ComboBox<String> format) {

        Profile p = new Profile();
        p.setName(name.getText());
        p.setRotation((int) rot.getValue());
        p.setBrightness((float) b.getValue());
        p.setContrast((float) c.getValue());
        p.setSplitOnBarcode(split.isSelected());
        p.setExportFormat(format.getValue());
        return p;
    }

    public static void updateProfile(Profile p, TextField name, Slider rot, Slider b,
                                     Slider c, CheckBox split, ComboBox<String> format) {

        p.setName(name.getText());
        p.setRotation((int) rot.getValue());
        p.setBrightness((float) b.getValue());
        p.setContrast((float) c.getValue());
        p.setSplitOnBarcode(split.isSelected());
        p.setExportFormat(format.getValue());
    }
}
