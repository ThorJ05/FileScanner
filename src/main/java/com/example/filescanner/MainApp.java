package com.example.filescanner;

import com.example.filescanner.BLL.UserManager;
import com.example.filescanner.GUI.Controllers.SceneController;
import com.example.filescanner.Util.PasswordUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // ONE-TIME migration — delete this line after running the app once!
        new UserManager().rehashAllPlainTextPasswords();

        SceneController.setStage(stage);

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/example/filescanner/Login.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("Tiff Logic");
        stage.setWidth(900);
        stage.setHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}