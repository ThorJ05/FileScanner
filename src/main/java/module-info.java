module com.example.filescanner {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.filescanner to javafx.fxml;
    exports com.example.filescanner;
    exports com.example.filescanner.GUI;
    opens com.example.filescanner.GUI to javafx.fxml;
}