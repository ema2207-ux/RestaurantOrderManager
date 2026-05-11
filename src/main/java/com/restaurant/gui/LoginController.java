package com.restaurant.gui;

import com.restaurant.model.Employee;
import com.restaurant.service.EmployeeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private PasswordField pinField;
    @FXML private Label errorLabel;

    private static Employee currentUser;

    public static Employee getCurrentUser() { return currentUser; }

    @FXML
    private void handleLogin() {
        String pin = pinField.getText();
        Employee employee = EmployeeService.getInstance().login(pin);

        if (employee != null) {
            currentUser = employee;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("main_view.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) pinField.getScene().getWindow();
                stage.setTitle("RestoManager - " + employee.getName() + " (" + employee.getRole() + ")");
                stage.setScene(new Scene(root, 1280, 850));
                stage.setFullScreen(false); // Removed fullscreen
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("PIN incorect!");
        }
    }

    @FXML
    private void handleShowRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) pinField.getScene().getWindow();
            stage.setTitle("RestoManager - Înregistrare Staff Nou");
            stage.setScene(new Scene(root, 400, 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
