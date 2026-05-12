package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

public class SceneController {

    private static Stage mainStage;
    private static User currentUser;

    private static final Deque<String> history = new ArrayDeque<>();
    private static String currentFxml;

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
        loadScene(fxmlFile, true);
    }

    private static void loadScene(String fxmlFile, boolean recordHistory) {
        try {
            if (recordHistory && currentFxml != null) {
                history.push(currentFxml);
            }

            URL resource = SceneController.class.getResource("/com/example/filescanner/" + fxmlFile);
            if (resource == null) {
                System.err.println("FXML not found: " + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            mainStage.setScene(new Scene(root));
            mainStage.show();

            currentFxml = fxmlFile;

        } catch (Exception e) {
            System.err.println("Failed to load scene: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public static void goBack() {
        if (history.isEmpty()) return;
        String previous = history.pop();
        loadScene(previous, false);
    }

    public static void clearHistory() {
        history.clear();
        currentFxml = null;
    }
}
