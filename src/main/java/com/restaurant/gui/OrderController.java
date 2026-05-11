package com.restaurant.gui;

import com.restaurant.model.MenuItem;
import com.restaurant.model.Table;
import com.restaurant.service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.List;

public class OrderController {
    @FXML private Label tableTitleLabel;
    @FXML private TextField searchMenuField;
    @FXML private ListView<String> menuListView;
    @FXML private ListView<String> orderItemsListView;
    @FXML private Label totalLabel;
    @FXML private RadioButton cashRadio;

    private static Table currentTable;
    private final RestaurantService restaurantService = new RestaurantService();

    public static void setTable(Table table) {
        currentTable = table;
    }

    @FXML
    public void initialize() {
        if (currentTable != null) {
            tableTitleLabel.setText("Comandă Masa " + currentTable.getId());
            loadMenu();
            refreshOrderItems();
        }

        searchMenuField.textProperty().addListener((obs, oldV, newV) -> filterMenu(newV));

        menuListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = menuListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    addSelectedProduct(selected.split(" - ")[0]);
                }
            }
        });
    }

    private void loadMenu() {
        menuListView.getItems().clear();
        for (MenuItem item : MenuService.getInstance().findAll()) {
            menuListView.getItems().add(item.getName() + " - " + item.calculatePrice() + " RON");
        }
    }

    private void filterMenu(String query) {
        menuListView.getItems().clear();
        String lowQ = query.toLowerCase();
        for (MenuItem item : MenuService.getInstance().findAll()) {
            if (item.getName().toLowerCase().contains(lowQ)) {
                menuListView.getItems().add(item.getName() + " - " + item.calculatePrice() + " RON");
            }
        }
    }

    private void addSelectedProduct(String name) {
        MenuItem item = MenuService.getInstance().findAll().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst().orElse(null);

        if (item != null) {
            restaurantService.addItemToTable(currentTable.getId(), item);
            AuditService.getInstance().logAction("Added Product: " + item.getName() + " to Table " + currentTable.getId());
            if (!currentTable.isOccupied()) {
                currentTable.setOccupied(true);
                TableService.getInstance().update(currentTable.getId(), true);
            }
            refreshOrderItems();
        }
    }

    private void refreshOrderItems() {
        orderItemsListView.getItems().clear();
        List<MenuItem> items = restaurantService.getOrderItems(currentTable.getId());
        for (MenuItem item : items) {
            orderItemsListView.getItems().add(item.getName() + " - " + item.calculatePrice() + " RON");
        }
        totalLabel.setText(String.format("Total: %.2f RON", restaurantService.calculateTableTotal(currentTable.getId())));
    }

    @FXML
    private void handleFinishOrder() {
        if (!currentTable.isOccupied()) return;

        double total = restaurantService.calculateTableTotal(currentTable.getId());
        AuditService.getInstance().logAction("Finished Order: Table " + currentTable.getId() + ", Total: " + total + " RON");
        BillService.getInstance().create(0, total);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Plată reușită pentru Masa " + currentTable.getId());
        alert.showAndWait();

        currentTable.setOccupied(false);
        TableService.getInstance().update(currentTable.getId(), false);
        restaurantService.clearTableOrder(currentTable.getId());
        handleBack();
    }

    @FXML
    private void handleClearOrder() {
        AuditService.getInstance().logAction("Cleared Order: Table " + currentTable.getId());
        restaurantService.clearTableOrder(currentTable.getId());
        refreshOrderItems();
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("main_view.fxml"));
            Stage stage = (Stage) tableTitleLabel.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 850);

            // Web-like fade transition
            root.setOpacity(0);
            stage.setScene(scene);
            stage.setFullScreen(false);

            FadeTransition ft = new FadeTransition(Duration.millis(400), root);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
