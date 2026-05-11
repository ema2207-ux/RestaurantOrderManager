package com.restaurant.gui;

import com.restaurant.model.Employee;
import com.restaurant.model.Table;
import com.restaurant.service.AuditService;
import com.restaurant.service.EmployeeService;
import com.restaurant.service.TableService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.List;

public class RestaurantController {

    @FXML private ListView<String> kitchenOrdersList;

    @FXML private TabPane mainTabPane;
    @FXML private Tab kitchenTab;
    @FXML private FlowPane tablesPane;
    @FXML private FlowPane terracePane;
    @FXML private HBox staffManagementBox;

    @FXML private TextField employeeNameField;
    @FXML private ComboBox<String> employeeRoleCombo;
    @FXML private TextField employeePinField;
    @FXML private TextArea logArea;

    @FXML
    public void initialize() {
        if (employeeRoleCombo != null) {
            employeeRoleCombo.getItems().addAll("Chelner", "Bucatar", "Manager", "Barman");
        }

        Employee user = LoginController.getCurrentUser();
        // Kitchen tab and Staff Management only for Managers
        if (user != null && !"Manager".equals(user.getRole())) {
            if (mainTabPane != null && kitchenTab != null) {
                mainTabPane.getTabs().remove(kitchenTab);
            }
            if (staffManagementBox != null) {
                staffManagementBox.setVisible(false);
                staffManagementBox.setManaged(false);
            }
        }

        // Just use kitchenOrdersList to suppress warning
        if (kitchenOrdersList != null) {
            kitchenOrdersList.getItems().add("Welcome, " + (user != null ? user.getName() : "Guest") + "!");
        }

        loadTables();
        log("Application initialized. Select a table to manage orders.");
    }

    private void loadTables() {
        if (tablesPane == null) return;
        tablesPane.getChildren().clear();
        if (terracePane != null) terracePane.getChildren().clear();

        for (Table table : TableService.getInstance().findAll()) {
            VBox tableBox = createTableNode(table);

            // Distribute tables to different panes based on ID
            // r1-r10 (let's say IDs 1-10) -> tablesPane
            // t1-t5 (let's say IDs 11-15) -> terracePane
            if (table.getId() >= 11 && terracePane != null) {
                terracePane.getChildren().add(tableBox);
            } else {
                tablesPane.getChildren().add(tableBox);
            }
        }
    }

    private VBox createTableNode(Table table) {
        VBox tableBox = new VBox(8);
        tableBox.setAlignment(javafx.geometry.Pos.CENTER);
        tableBox.getStyleClass().add("table-node");

        StackPane stack = new StackPane();
        Rectangle rect = new Rectangle(100, 100);
        rect.setArcWidth(20);
        rect.setArcHeight(20);

        rect.setStrokeWidth(1);
        rect.setStroke(Color.web("#dee2e6"));

        rect.setFill(table.isOccupied() ? Color.web("#dc3545") : Color.web("#28a745"));

        // Label handling R for Restaurant and T for Terrace based on ID
        String prefix = (table.getId() >= 11) ? "T" : "R";
        int displayId = (table.getId() >= 11) ? table.getId() - 10 : table.getId();
        Label label = new Label(prefix + displayId);

        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: 900;");

        stack.getChildren().addAll(rect, label);

        tableBox.getChildren().add(stack);
        tableBox.setOnMouseClicked(event -> {
            try {
                OrderController.setTable(table);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("order_view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) tablesPane.getScene().getWindow();
                Scene scene = new Scene(root, 1280, 850);

                root.setOpacity(0);
                stage.setScene(scene);
                stage.setFullScreen(false);

                FadeTransition ft = new FadeTransition(Duration.millis(400), root);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
            } catch (Exception e) {
                log("Error loading order view: " + e.getMessage());
            }
        });
        return tableBox;
    }

    @FXML
    private void handleSaveEmployee() {
        String name = employeeNameField.getText();
        String role = employeeRoleCombo.getValue();
        String pin = employeePinField != null ? employeePinField.getText() : null;

        if (name == null || name.isEmpty() || role == null || pin == null || pin.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Data");
            alert.setContentText("Please fill in name, role, and PIN.");
            alert.showAndWait();
            return;
        }

        EmployeeService.getInstance().create(name, role, pin);
        AuditService.getInstance().logAction("Registered Staff: " + name + " (" + role + ")");
        log("Added new employee: " + name + " (" + role + ")");
        employeeNameField.clear();
        employeeRoleCombo.setValue(null);
        if (employeePinField != null) employeePinField.clear();
    }

    @FXML
    private void handleLogout() {
        try {
            AuditService.getInstance().logAction("User Logout");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            stage.setTitle("RestoManager - Login");
            stage.setScene(new Scene(root, 400, 500));
            stage.setFullScreen(false); // Do not keep fullscreen after logout
            stage.centerOnScreen();
        } catch (Exception e) {
            log("Error logging out: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshData() {
        loadTables();
        AuditService.getInstance().logAction("Manual Data Refresh");
        log("Data manually refreshed.");
    }

    @FXML
    private void handleExit() {
        AuditService.getInstance().logAction("System Exit");
        Platform.exit();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Despre");
        alert.setHeaderText("RestoManager Pro v2.0");
        alert.setContentText("Dezvoltat de: Radulescu Emanuel-Andrei\nDisciplina: POO II - 2026");
        alert.showAndWait();
    }

    private void log(String message) {
        if (logArea != null) {
            logArea.appendText("[" + java.time.LocalTime.now().withNano(0) + "] " + message + "\n");
        }
    }
}
