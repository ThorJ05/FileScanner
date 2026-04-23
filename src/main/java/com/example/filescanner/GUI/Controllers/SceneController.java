package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {

    private static Stage mainStage;
    private static User currentUser;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void switchTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(
                    SceneController.class.getResource("/com/example/filescanner/" + fxmlFile)
            );
            mainStage.setScene(new Scene(root));
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}