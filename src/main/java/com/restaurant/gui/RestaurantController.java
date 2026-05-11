package com.restaurant.gui;

import com.restaurant.model.Employee;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Table;
import com.restaurant.service.EmployeeService;
import com.restaurant.service.MenuService;
import com.restaurant.service.RestaurantService;
import com.restaurant.service.TableService;
import com.restaurant.service.BillService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class RestaurantController {

    @FXML private ListView<String> menuListView;
    @FXML private FlowPane tablesPane;
    @FXML private Tab kitchenTab;
    @FXML private TabPane mainTabPane;
    @FXML private ListView<String> kitchenOrdersList;

    @FXML private TextField employeeNameField;
    @FXML private ComboBox<String> employeeRoleCombo;
    @FXML private TextArea logArea;

    @FXML private Label selectedTableLabel;
    @FXML private ListView<String> orderItemsListView;
    @FXML private Label totalLabel;
    @FXML private RadioButton cashRadio;
    @FXML private RadioButton cardRadio;
    @FXML private VBox selectedTablePane;

    private RestaurantService restaurantService = new RestaurantService();
    private Table selectedTable = null;

    @FXML
    public void initialize() {
        if (employeeRoleCombo != null) {
            employeeRoleCombo.getItems().addAll("Chelner", "Bucatar", "Manager", "Barman");
        }
        Employee user = LoginController.getCurrentUser();
        // Kitchen tab only for Managers or special roles
        if (!"Manager".equals(user.getRole())) {
            mainTabPane.getTabs().remove(kitchenTab);
        }

        loadMenu();
        loadTables();

        menuListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedTable != null) {
                String selectedItem = menuListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String selectedName = selectedItem.split(" - ")[0];
                    addSelectedProductToTable(selectedName);
                }
            }
        });
        log("Application initialized. Select a table to manage orders.");
    }

    private void addSelectedProductToTable(String productName) {
        if (!selectedTable.isOccupied()) {
            selectedTable.setOccupied(true);
            TableService.getInstance().update(selectedTable.getId(), true);
            loadTables();
        }

        MenuItem item = MenuService.getInstance().findAll().stream()
                .filter(m -> m.getName().equals(productName))
                .findFirst().orElse(null);

        if (item != null) {
            restaurantService.addItemToTable(selectedTable.getId(), item);
            refreshOrderItems();
            log("Added " + productName + " to Table " + selectedTable.getId());
        }
    }

    private void refreshOrderItems() {
        if (selectedTable == null) return;
        orderItemsListView.getItems().clear();
        List<MenuItem> items = restaurantService.getOrderItems(selectedTable.getId());
        for (MenuItem item : items) {
            orderItemsListView.getItems().add(item.getName() + " - " + item.calculatePrice() + " RON");
        }
        if (totalLabel != null) {
            totalLabel.setText(String.format("Total: %.2f RON", restaurantService.calculateTableTotal(selectedTable.getId())));
        }
    }

    private void loadMenu() {
        menuListView.getItems().clear();
        for (MenuItem item : MenuService.getInstance().findAll()) {
            menuListView.getItems().add(item.getName() + " - " + item.calculatePrice() + " RON");
        }
    }

    private void loadTables() {
        tablesPane.getChildren().clear();
        for (Table table : TableService.getInstance().findAll()) {
            VBox tableBox = new VBox(5);
            tableBox.setAlignment(javafx.geometry.Pos.CENTER);

            Rectangle rect = new Rectangle(70, 70);
            rect.setArcWidth(10);
            rect.setArcHeight(10);

            if (selectedTable != null && selectedTable.getId() == table.getId()) {
                rect.setStrokeWidth(3);
                rect.setStroke(Color.GOLD);
            } else {
                rect.setStrokeWidth(1);
                rect.setStroke(Color.DARKSLATEGRAY);
            }

            rect.setFill(table.isOccupied() ? Color.TOMATO : Color.MEDIUMSEAGREEN);

            Label label = new Label("Masa " + table.getId());
            label.setTextFill(Color.WHITE);
            label.setStyle("-fx-font-weight: bold;");

            tableBox.getChildren().addAll(rect, label);
            tableBox.setOnMouseClicked(event -> {
                selectedTable = table;
                if (selectedTableLabel != null) {
                    selectedTableLabel.setText("Managing Table " + table.getId());
                }
                loadTables();
                refreshOrderItems();
            });

            tablesPane.getChildren().add(tableBox);
        }
    }

    @FXML
    private void handleFinishOrder() {
        if (selectedTable == null || !selectedTable.isOccupied()) {
            showError("Error", "No active order to finish.");
            return;
        }

        double total = restaurantService.calculateTableTotal(selectedTable.getId());
        String method = (cashRadio != null && cashRadio.isSelected()) ? "Cash" : "Card";

        BillService.getInstance().create(0, total);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText("Receipt for Table " + selectedTable.getId());
        alert.setContentText(String.format("Total Amount: %.2f RON\nPayment Method: %s\n\nThank you!", total, method));
        alert.showAndWait();

        selectedTable.setOccupied(false);
        TableService.getInstance().update(selectedTable.getId(), false);
        restaurantService.clearTableOrder(selectedTable.getId());

        log("Table " + selectedTable.getId() + " paid via " + method + " and is now FREE.");

        refreshOrderItems();
        loadTables();
    }

    @FXML
    private void handleSaveEmployee() {
        String name = employeeNameField.getText();
        String role = employeeRoleCombo.getValue();

        if (name == null || name.isEmpty() || role == null) {
            showError("Invalid Data", "Please fill in both name and role.");
            return;
        }

        EmployeeService.getInstance().create(name, role);
        log("Added new employee: " + name + " (" + role + ")");
        employeeNameField.clear();
        employeeRoleCombo.setValue(null);
    }

    @FXML
    private void handleRefreshData() {
        loadMenu();
        loadTables();
        log("Data manually refreshed from database.");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Restaurant Manager v2.0");
        alert.setContentText("Project 2026 - POO II Integration with DB and JavaFX.");
        alert.showAndWait();
    }

    @FXML
    private void handleClearOrder() {
        if (selectedTable != null) {
            restaurantService.clearTableOrder(selectedTable.getId());
            refreshOrderItems();
            log("Cleared items for Table " + selectedTable.getId());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            stage.setTitle("RestoManager - Login");
            stage.setScene(new Scene(root, 400, 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(String message) {
        if (logArea != null) {
            logArea.appendText("[" + java.time.LocalTime.now().withNano(0) + "] " + message + "\n");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
