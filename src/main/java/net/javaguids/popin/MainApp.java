package net.javaguids.popin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/net/javaguids/popin/views/login.fxml")
        );
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("PopIn – Login");
        stage.setScene(scene);

        // Let JavaFX size the window to fit the FXML
        stage.sizeToScene();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}