package com.restaurant.gui;

import com.restaurant.model.Employee;
import com.restaurant.model.Table;
import com.restaurant.model.KitchenOrder;
import com.restaurant.model.MenuItem;
import com.restaurant.service.AuditService;
import com.restaurant.service.EmployeeService;
import com.restaurant.service.TableService;
import com.restaurant.service.RestaurantService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RestaurantController {

    @FXML private VBox kitchenBox; // Container-ul pentru chenarele din bucătărie
    @FXML private ListView<String> kitchenOrdersList;


    @FXML private TabPane mainTabPane;
    @FXML private Tab kitchenTab;
    @FXML private Tab barTab; // Vom adăuga acest tab în FXML
    @FXML private Tab reportsTab; // Tab nou pentru Manager
    @FXML private VBox reportsBox; // Container pentru statistici
    @FXML private VBox barBox; // Container pentru comenzile barmanului
    @FXML private FlowPane tablesPane;
    @FXML private FlowPane terracePane;
    @FXML private HBox staffManagementBox;

    @FXML private TextField employeeNameField;
    @FXML private ComboBox<String> employeeRoleCombo;
    @FXML private TextField employeePinField;
    @FXML private TextArea logArea;

    private final RestaurantService restaurantService = new RestaurantService();

    @FXML
    public void initialize() {
        if (employeeRoleCombo != null) {
            employeeRoleCombo.getItems().addAll("Chelner", "Bucatar", "Manager", "Barman");
        }

        Employee user = LoginController.getCurrentUser();
        
        // Logica de filtrare interfata in functie de rol
        if (user != null) {
            String role = user.getRole();
            
            if ("Bucatar".equals(role)) {
                // Pentru BUCATAR: Ascundem tab-urile de mese (Restaurant si Terasa) si Staff Management
                if (mainTabPane != null) {
                    // Eliminam tab-urile de mese (index 0 si 1 de obicei, dar le cautam dupa titlu/referinta)
                    mainTabPane.getTabs().removeIf(tab -> 
                        tab.getText().contains("RESTAURANT") || tab.getText().contains("TERASĂ")
                    );
                    // Selectam automat tab-ul de Bucatarie care ramane singurul/principalul
                    mainTabPane.getSelectionModel().select(kitchenTab);
                }
                if (staffManagementBox != null) {
                    staffManagementBox.setVisible(false);
                    staffManagementBox.setManaged(false);
                }
            } else if ("Barman".equals(role)) {
                // Dacă este barman, păstrăm doar tab-ul de bar (pe care îl vom defini ca având textul BAR)
                if (mainTabPane != null) {
                    mainTabPane.getTabs().removeIf(tab -> !tab.getText().contains("BAR"));
                    mainTabPane.getSelectionModel().select(barTab);
                }
            } else if ("Chelner".equals(role)) {
                if (mainTabPane != null) {
                    mainTabPane.getTabs().remove(kitchenTab);
                    mainTabPane.getTabs().remove(barTab);
                    mainTabPane.getTabs().remove(reportsTab);
                }
            }
            
            if (!"Manager".equals(role)) {
                if (mainTabPane != null) {
                    mainTabPane.getTabs().remove(reportsTab);
                }
                if (staffManagementBox != null) {
                    staffManagementBox.setVisible(false);
                    staffManagementBox.setManaged(false);
                }
            }
        }

        // Just use kitchenOrdersList to suppress warning
        if (kitchenOrdersList != null) {
            kitchenOrdersList.getItems().add("Welcome, " + (user != null ? user.getName() : "Guest") + "!");
        }

        loadTables();
        loadKitchenOrders();
        loadBarOrders();
        loadReports();
        
        // Add a listener to refresh reports whenever the Reports tab is selected
        if (mainTabPane != null && reportsTab != null) {
            mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab == reportsTab) {
                    loadReports();
                    log("Raportul de încasări a fost actualizat.");
                }
            });
        }
        
        log("Application initialized. Select a table to manage orders.");
    }

    private void loadKitchenOrders() {
        if (kitchenBox == null) return;
        kitchenBox.getChildren().clear();

        List<KitchenOrder> activeOrders = restaurantService.getActiveKitchenOrders();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (KitchenOrder order : activeOrders) {
            if (order.isCompleted() || !"BUCATARIE".equals(order.getDestination())) continue;

            VBox orderCard = new VBox(12);
            orderCard.getStyleClass().addAll("order-card", "order-card-kitchen");

            HBox header = new HBox(10);
            header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label title = new Label("MASA " + order.getTableId());
            title.getStyleClass().add("order-card-title");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label timeLabel = new Label(order.getTimestamp().format(timeFormatter));
            timeLabel.getStyleClass().add("order-card-time");
            header.getChildren().addAll(title, spacer, timeLabel);

            VBox itemsBox = new VBox(5);
            for (MenuItem item : order.getItems()) {
                Label itemLabel = new Label("• " + item.getName());
                itemLabel.getStyleClass().add("order-card-item");
                itemsBox.getChildren().add(itemLabel);
            }

            Button btnComplete = new Button("MARCAT GATA");
            btnComplete.getStyleClass().add("order-complete-btn");
            btnComplete.setMaxWidth(Double.MAX_VALUE);
            btnComplete.setOnAction(e -> {
                restaurantService.completeKitchenOrder(order);
                AuditService.getInstance().logAction("Comanda Finalizata Masa " + order.getTableId());
                loadKitchenOrders();
                loadBarOrders();
                showBellNotification(order.getTableId());
            });

            orderCard.getChildren().addAll(header, itemsBox, btnComplete);
            kitchenBox.getChildren().add(orderCard);
        }
    }

    private void loadBarOrders() {
        if (barBox == null) return;
        barBox.getChildren().clear();

        List<KitchenOrder> activeOrders = restaurantService.getActiveKitchenOrders();

        for (KitchenOrder order : activeOrders) {
            if (order.isCompleted() || !"BAR".equals(order.getDestination())) continue;

            VBox orderCard = createOrderCard(order);
            barBox.getChildren().add(orderCard);
        }
    }

    private VBox createOrderCard(KitchenOrder order) {
        String type = order.getDestination();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        VBox orderCard = new VBox(12);
        orderCard.getStyleClass().addAll("order-card", "order-card-bar");
        
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label title = new Label("MASA " + order.getTableId() + " (" + type + ")");
        title.getStyleClass().add("order-card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label timeLabel = new Label(order.getTimestamp().format(timeFormatter));
        timeLabel.getStyleClass().add("order-card-time");
        header.getChildren().addAll(title, spacer, timeLabel);

        VBox itemsBox = new VBox(5);
        for (com.restaurant.model.MenuItem item : order.getItems()) {
            Label itemLabel = new Label("• " + item.getName());
            itemLabel.getStyleClass().add("order-card-item");
            itemsBox.getChildren().add(itemLabel);
        }

        Button btnComplete = new Button("MARCAT GATA");
        btnComplete.getStyleClass().add("order-complete-btn");
        btnComplete.setMaxWidth(Double.MAX_VALUE);
        btnComplete.setOnAction(e -> {
            restaurantService.completeKitchenOrder(order);
            AuditService.getInstance().logAction("Comanda Finalizata ("+type+") Masa " + order.getTableId());
            loadKitchenOrders();
            loadBarOrders();
            showBellNotification(order.getTableId());
        });

        orderCard.getChildren().addAll(header, itemsBox, btnComplete);
        return orderCard;
    }

    private void showBellNotification(int tableId) {
        // Logica pentru clopotel de notificare (simulăm vizual)
        log("!!! CLOPOTEL: Comanda pentru Masa " + tableId + " este GATA!");

        // Putem afișa o alertă mică sau un indicator
        Platform.runLater(() -> {
            Alert tip = new Alert(Alert.AlertType.INFORMATION);
            tip.setTitle("Notificare Bucatarie");
            tip.setHeaderText("Comanda Gata!");
            tip.setContentText("Masa " + tableId + " poate fi servita. 🔔");
            tip.show();
        });
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
        loadKitchenOrders();
        loadBarOrders();
        loadReports();
        AuditService.getInstance().logAction("Manual Data Refresh");
        log("Data manually refreshed.");

        // Show an alert if reports are still 0 after manual refresh
        List<com.restaurant.model.Bill> bills = com.restaurant.service.BillService.getInstance().findAll();
        if (bills.isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenție");
                alert.setHeaderText("Nu au fost găsite încasări");
                alert.setContentText("Dacă ai făcut vânzări recent, verifică dacă plata a fost finalizată cu succes.");
                alert.show();
            });
        }
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

    private void loadReports() {
        if (reportsBox == null) return;
        reportsBox.getChildren().clear();

        List<com.restaurant.model.Bill> bills;
        try {
            bills = com.restaurant.service.BillService.getInstance().findAll();
            log("Incasari gasite in DB: " + bills.size());
        } catch (Exception e) {
            log("Error loading reports: " + e.getMessage());
            return;
        }

        if (bills.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setStyle("-fx-padding: 60; -fx-text-fill: #9ca3af;");
            Label emptyLabel = new Label("Nu exista incasari inregistrate.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #9ca3af;");
            emptyBox.getChildren().add(emptyLabel);
            reportsBox.getChildren().add(emptyBox);
            return;
        }

        java.util.Map<String, Double> stats = new java.util.HashMap<>();

        for (com.restaurant.model.Bill bill : bills) {
            String waiter = bill.getEmployeeName();
            if (waiter == null) waiter = "Manager"; // Default to Manager if null
            stats.put(waiter, stats.getOrDefault(waiter, 0.0) + bill.getAmount());
            log("Procesat factura: " + waiter + " - " + bill.getAmount());
        }

        Label title = new Label("RAPOART INCASARI PE OSPATAR");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        reportsBox.getChildren().add(title);
        reportsBox.getChildren().add(new Separator());

        if (!stats.isEmpty()) {
            stats.forEach((waiter, total) -> {
                HBox row = new HBox(20);
                row.getStyleClass().add("stats-card");
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                Label nameLabel = new Label(waiter);
                nameLabel.getStyleClass().add("stats-label");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label amountLabel = new Label(String.format("%.2f RON", total));
                amountLabel.getStyleClass().add("stats-amount");

                row.getChildren().addAll(nameLabel, spacer, amountLabel);
                reportsBox.getChildren().add(row);
            });
        }
    }

    private void log(String message) {
        if (logArea != null) {
            logArea.appendText("[" + java.time.LocalTime.now().withNano(0) + "] " + message + "\n");
        }
    }
}
