package net.javaguids.popin.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import net.javaguids.popin.database.UserDAO;
import net.javaguids.popin.models.User;

import java.util.List;

public class UserListController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Number> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Configure how columns read data from User
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()));

        usernameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUsername()));

        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRole() != null
                                ? cellData.getValue().getRole().getName()
                                : ""
                ));

        loadUsers();
    }

    private void loadUsers() {
        List<User> users = userDAO.listAll();
        userTable.getItems().setAll(users);
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("No user selected", "Please select a user to delete.");
            return;
        }

        // Optional: prevent deleting yourself or some protected admin
        // if (/* some check */) { ... }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm delete");
        confirm.setHeaderText("Delete user");
        confirm.setContentText("Are you sure you want to delete user: " + selected.getUsername() + "?");

        // Show dialog and check result
        confirm.showAndWait().ifPresent(buttonType -> {
            switch (buttonType.getButtonData()) {
                case OK_DONE, YES -> {
                    boolean deleted = userDAO.deleteById(selected.getId());
                    if (deleted) {
                        showInfo("User deleted", "User was deleted successfully.");
                        loadUsers();
                    } else {
                        showError("Delete failed", "Could not delete user from database.");
                    }
                }
                default -> {
                    // Cancel / close – do nothing
                }
            }
        });
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) userTable.getScene().getWindow();
        stage.close();
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }

    private void showInfo(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }
}