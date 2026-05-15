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
import javafx.scene.layout.*;
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
            menuListView.getItems().add(item.getName() + "###" + item.calculatePrice());
        }
        setupCustomMenuCellFactory();
    }

    private void filterMenu(String query) {
        menuListView.getItems().clear();
        String lowQ = query.toLowerCase();
        for (MenuItem item : MenuService.getInstance().findAll()) {
            if (item.getName().toLowerCase().contains(lowQ)) {
                menuListView.getItems().add(item.getName() + "###" + item.calculatePrice());
            }
        }
    }

    private void setupCustomMenuCellFactory() {
        menuListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    String[] parts = item.split("###");
                    String name = parts[0];
                    String price = parts[1];

                    HBox container = new HBox();
                    container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    container.setSpacing(10);
                    container.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));

                    VBox textContainer = new VBox(2);
                    Label nameLabel = new Label(name);
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");
                    
                    Label priceLabel = new Label(price + " RON");
                    priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                    
                    textContainer.getChildren().addAll(nameLabel, priceLabel);
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Button btnAdd = new Button("+");
                    btnAdd.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50;");
                    btnAdd.setPrefSize(30, 30);
                    btnAdd.setFocusTraversable(false);
                    // Adăugarea prin buton sau dublu click deja existent
                    btnAdd.setOnAction(e -> addSelectedProduct(name));

                    container.getChildren().addAll(textContainer, spacer, btnAdd);
                    setGraphic(container);
                    getStyleClass().add("menu-item-cell");
                }
            }
        });
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
            orderItemsListView.getItems().add(item.getName() + "###" + item.calculatePrice());
        }
        setupCustomOrderCellFactory();
        totalLabel.setText(String.format("Total: %.2f RON", restaurantService.calculateTableTotal(currentTable.getId())));
    }

    private void setupCustomOrderCellFactory() {
        orderItemsListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String[] parts = item.split("###");
                    String name = parts[0];
                    String price = parts[1];

                    HBox container = new HBox();
                    container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    container.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));

                    Label nameLabel = new Label(name);
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Label priceLabel = new Label(price + " RON");
                    priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

                    container.getChildren().addAll(nameLabel, spacer, priceLabel);
                    setGraphic(container);
                }
            }
        });
    }

     @FXML
     private void handleFinishOrder() {
         if (!currentTable.isOccupied()) return;

         double total = restaurantService.calculateTableTotal(currentTable.getId());
         String paymentMethod = (cashRadio != null && cashRadio.isSelected()) ? "Numerar" : "Card";
         String waiterName = LoginController.getCurrentUser() != null ? LoginController.getCurrentUser().getName() : "Necunoscut";

         AuditService.getInstance().logAction("Finalizat Plata: Masa " + currentTable.getId() + ", Total: " + total + " RON, Metoda: " + paymentMethod + ", Ospatar: " + waiterName);
         
         // Create an order in database first (required for foreign key constraint)
         int orderId = restaurantService.createOrderInDatabase(currentTable.getId(), waiterName);
         
         // Save bill with the actual order_id
         BillService.getInstance().create(orderId, total, paymentMethod, waiterName);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Plată Finalizată");
        alert.setHeaderText("NOTĂ DE PLATĂ - MASA " + currentTable.getId());
        alert.setContentText(String.format(
            "Produsele au fost achitate cu succes.\n\n" +
            "Suma totală: %.2f RON\n" +
            "Metodă plată: %s\n\n" +
            "Masa este acum liberă!", total, paymentMethod));
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
            // Trimite comanda la bucătărie înainte de a ieși dacă există iteme
            List<MenuItem> currentItems = restaurantService.getOrderItems(currentTable.getId());
            if (!currentItems.isEmpty()) {
                restaurantService.sendToKitchen(currentTable.getId(), currentItems);
                AuditService.getInstance().logAction("Trimis la bucatarie Masa " + currentTable.getId());
            }

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
