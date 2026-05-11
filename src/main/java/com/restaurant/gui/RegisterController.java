package com.restaurant.gui;

import com.restaurant.service.EmployeeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField pinField;

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("Chelner", "Bucatar", "Manager", "Barman");
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String role = roleCombo.getValue();
        String pin = pinField.getText();

        if (name != null && !name.isEmpty() && role != null && pin != null && !pin.isEmpty()) {
            EmployeeService.getInstance().create(name, role, pin);
            handleBack();
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login_view.fxml"));
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

