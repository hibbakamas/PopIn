package net.javaguids.popin.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private static Stage primaryStage;

    // Map simple keys -> FXML paths
    private static final Map<String, String> VIEWS = new HashMap<>();

    static {
        // ⚠️ These keys must match how you'll call switchTo(...)
        VIEWS.put("login",               "/net/javaguids/popin/views/login.fxml");
        VIEWS.put("signup",              "/net/javaguids/popin/views/sign-up.fxml");
        VIEWS.put("adminDashboard",      "/net/javaguids/popin/views/admin-dashboard.fxml");
        VIEWS.put("organizerDashboard",  "/net/javaguids/popin/views/organizer-dashboard.fxml");
        VIEWS.put("attendeeDashboard",   "/net/javaguids/popin/views/attendee-dashboard.fxml");
        VIEWS.put("adminEventList",      "/net/javaguids/popin/views/admin-event-list.fxml");
        VIEWS.put("analytics",           "/net/javaguids/popin/views/analytics.fxml");
        VIEWS.put("attendeeList",        "/net/javaguids/popin/views/attendee-list.fxml");
        VIEWS.put("createEvent",         "/net/javaguids/popin/views/create-event.fxml");
        VIEWS.put("eventDetails",        "/net/javaguids/popin/views/event-details.fxml");
        VIEWS.put("eventList",           "/net/javaguids/popin/views/event-list.fxml");
        VIEWS.put("myEvents",            "/net/javaguids/popin/views/my-events.fxml");
        VIEWS.put("myRegistrations",     "/net/javaguids/popin/views/my-registrations.fxml");
        VIEWS.put("profile",             "/net/javaguids/popin/views/profile.fxml");
        VIEWS.put("reports",             "/net/javaguids/popin/views/reports-view.fxml");
        VIEWS.put("userList",            "/net/javaguids/popin/views/user-list.fxml");
    }

    /** Called once from MainApp.start(...) */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
    }

    /**
     * Main navigation API.
     * Loads the view identified by 'key' into the primary stage and returns its controller.
     */
    public static <T> T switchTo(String key, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager.primaryStage is not set. Call setPrimaryStage() first.");
        }

        String fxmlPath = VIEWS.get(key);
        if (fxmlPath == null) {
            throw new IllegalArgumentException("No view registered for key: " + key);
        }

        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(root));
            primaryStage.sizeToScene();
            primaryStage.show();

            return loader.getController();
        } catch (IOException e) {
            System.err.println("SceneManager.switchTo error for key '" + key + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Still useful if you ever need to manually load an FXML
    public static FXMLLoader loadFXML(String fxmlPath) {
        return new FXMLLoader(SceneManager.class.getResource(fxmlPath));
    }
}
