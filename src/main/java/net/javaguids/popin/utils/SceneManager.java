package net.javaguids.popin.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    public static void showScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            stage.setTitle(title);
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // ⭐ Make the window match the FXML's preferred size
            stage.sizeToScene();

            // (Optional) prevent absurdly tiny windows
            stage.setMinWidth(600);
            stage.setMinHeight(400);

            stage.show();
        } catch (Exception e) {
            System.err.println("SceneManager error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static FXMLLoader loadFXML(String fxmlPath) {
        return new FXMLLoader(SceneManager.class.getResource(fxmlPath));
    }
}