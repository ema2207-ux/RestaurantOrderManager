package com.restaurant.gui;

import com.restaurant.service.TableService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RestaurantGui extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login_view.fxml"));
        primaryStage.setTitle("RestoManager - Pro Edition");
        primaryStage.setScene(new Scene(root, 400, 500));

        // Let the user start in windowed mode
        primaryStage.setFullScreen(false);
        primaryStage.setFullScreenExitHint("Apasă ESC pentru a ieși din Fullscreen");

        primaryStage.show();
    }

    @Override
    public void stop() {
        System.out.println("[INFO] Aplicația se închide. Resetăm starea meselor...");
        TableService.getInstance().resetAllTables();
        System.out.println("[INFO] Toate mesele au fost eliberate.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
