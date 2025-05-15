package ui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cbTipoAcesso;
    @FXML private Label lblMensagem;

    @FXML
    private void initialize() {
        // Configurações iniciais (opcional)
        cbTipoAcesso.setItems(FXCollections.observableArrayList(
            "Responsável",
            "Ajudante"
        ));
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String email = txtEmail.getText();
        String tipoAcesso = cbTipoAcesso.getValue();
        
        if (usuario.isEmpty() || email.isEmpty() || tipoAcesso == null) {
            lblMensagem.setText("Preencha todos os campos!");
        } else {
            lblMensagem.setText(String.format(
                "Usuário: %s | Email: %s | Tipo: %s", 
                usuario, email, tipoAcesso
            ));
            // Aqui você pode adicionar a lógica de autenticação
            // if (authenticate(usuario, email, tipoAcesso)) {           
                successLogin();
            // } else {
            //     lblMensagem.setText("Credenciais inválidas!");
            // }
        }
    }

    @FXML
    private void successLogin() {
        try {
            Parent dashboard = FXMLLoader.load(getClass().getResource("/ui/views/MainDashboard.fxml"));
            Stage currentStage = (Stage) txtUsuario.getScene().getWindow();
            
            currentStage.setScene(new Scene(dashboard));
            currentStage.setFullScreen(true); // Mantém tela cheia
            currentStage.show();
            
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erro ao carregar a tela principal: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCadastro() {
        lblMensagem.setText("Redirecionando para cadastro...");
        // Implemente a navegação para a tela de cadastro depois
    }

    /*private boolean authenticate(String usuario, String email, String tipoAcesso) {
        // Aqui você implementaria a lógica real de autenticação
        // Por enquanto, apenas simulação
        return true;
    } */

    private void showError(String message) {
        lblMensagem.setText(message);
        lblMensagem.setStyle("-fx-text-fill: red;");
    }
}