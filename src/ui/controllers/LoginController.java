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
    private void successLogin(){
         try {
            // Usando caminho absoluto como fallback
            URL fxmlLocation;
            try {
                // Primeiro tenta pelo classpath (recomendado)
                fxmlLocation = getClass().getResource("/ui/views/MainDashboard.fxml");
                if (fxmlLocation == null) {
                    // Fallback para caminho absoluto (apenas para desenvolvimento)
                    fxmlLocation = new File("src/ui/views/MainDashboard.fxml").toURI().toURL();
                    System.out.println("Usando fallback para: " + fxmlLocation);
                }
            } catch (Exception e) {
                throw new IOException("Não foi possível localizar o arquivo MainDashboard.fxml", e);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent dashboard = loader.load();
            
            // Configuração da nova cena
            Scene dashboardScene = new Scene(dashboard, 800, 600);
            
            // Obtém a janela atual e aplica a nova cena
            Stage currentStage = (Stage) txtUsuario.getScene().getWindow();
            currentStage.setScene(dashboardScene);
            currentStage.setTitle("PlantCare - Dashboard");
            currentStage.centerOnScreen();
            
        } catch (IOException e) {
            showError("Falha ao carregar o dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Mostra alerta detalhado em caso de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Carregamento");
            alert.setHeaderText("Não foi possível carregar a tela principal");
            alert.setContentText(e.toString());
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