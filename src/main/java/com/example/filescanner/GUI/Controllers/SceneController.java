package com.example.filescanner.GUI.Controllers;

import com.example.filescanner.BEE.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class SceneController {

    private static Stage mainStage;
    private static User currentUser;

    // History of visited FXML filenames (most recent on top)
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

            Parent root = FXMLLoader.load(
                    SceneController.class.getResource("/com/example/filescanner/" + fxmlFile)
            );
            mainStage.setScene(new Scene(root));
            mainStage.show();
            currentFxml = fxmlFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Go back to the previous scene if any
    public static void goBack() {
        if (history.isEmpty()) return;
        String previous = history.pop();
        loadScene(previous, false);
    }

    // Clear navigation history (useful on logout)
    public static void clearHistory() {
        history.clear();
        currentFxml = null;
    }
}