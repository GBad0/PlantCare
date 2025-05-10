package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainDashboardController {
    @FXML private StackPane contentArea;

    // Métodos para trocar as telas
    @FXML
    private void showPlantList() {
        loadView("ListaPlantas.fxml"); // Você precisará criar este arquivo depois
    }

    @FXML
    private void showNewPlantation() {
        showAlert("Funcionalidade", "Nova Plantação será implementada aqui");
    }

    @FXML
    private void showAddNotes() {
        showAlert("Funcionalidade", "Anotações serão implementadas aqui");
    }
    
    @FXML
    private void showNotes() {
        showAlert("Funcionalidade", "Anotações serão implementadas aqui");
    }

    @FXML
    private void showColheita() {
        showAlert("Funcionalidade", "colheita implementadas aqui");
    }

    @FXML
    private void showPrevisaotempo() {
        showAlert("Funcionalidade", "Previsao do tempo implementadas aqui");
    }


    @FXML
    private void handleLogout() {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/ui/views/Login.fxml"));
            contentArea.getScene().setRoot(loginView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/ui/views/" + fxmlFile));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            showAlert("Erro", "Não foi possível carregar a view: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
