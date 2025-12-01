package net.javaguids.popin;

import javafx.application.Application;
import javafx.stage.Stage;
import net.javaguids.popin.utils.SceneManager;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // hand primary stage to SceneManager
        SceneManager.setPrimaryStage(stage);

        // show initial view
        SceneManager.switchTo("login", "PopIn – Login");
    }

    public static void main(String[] args) {
        launch();
    }
}
