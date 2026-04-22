package com.example.filescanner;

import com.example.filescanner.GUI.Controllers.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Makes SceneController able to control the window.
        SceneController.setStage(stage);

        // Start the program in loginFXML instead of helloFXML. NOTICE MAKE LOGIN FXML
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("resources/com.example.filescanner.Login.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("File Scanner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
