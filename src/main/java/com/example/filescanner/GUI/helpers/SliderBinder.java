package com.example.filescanner.GUI.helpers;

import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class SliderBinder {

    public static void bind(Slider slider, TextField field, Runnable onChange,
                            double min, double max) {

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!field.isFocused()) {
                field.setText(String.valueOf((int) Math.round(newVal.doubleValue())));

            }
            onChange.run();
        });

        field.setOnAction(e -> {
            try {
                double v = Double.parseDouble(field.getText());
                slider.setValue(Math.min(max, Math.max(min, v)));
            } catch (Exception ignored) {}
            onChange.run();
        });
    }
}
