package ui.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Objects;
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
            return;
        }
        if (authenticate(usuario, email, tipoAcesso)) {
            successLogin();
        } else {
            lblMensagem.setText("Usuário ou dados inválidos!");
        }
    }

    @FXML
    private void successLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/MainDashboard.fxml"));
            Parent dashboard = loader.load();
            MainDashboardController controller = loader.getController();
            if (controller != null) {
                controller.setTipoAcessoUsuario(cbTipoAcesso.getValue());
            }
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
        String usuario = txtUsuario.getText();
        String email = txtEmail.getText();
        String tipoAcesso = cbTipoAcesso.getValue();
        if (usuario.isEmpty() || email.isEmpty() || tipoAcesso == null) {
            lblMensagem.setText("Preencha todos os campos!");
            return;
        }
        if (!isEmailValido(email)) {
            lblMensagem.setText("Digite um e-mail válido!");
            return;
        }
        if (registerUser(usuario, email, tipoAcesso)) {
            lblMensagem.setText("Usuário cadastrado com sucesso!");
        } else {
            lblMensagem.setText("Usuário já existe!");
        }
    }

    private boolean authenticate(String usuario, String email, String tipoAcesso) {
        File file = new File("data/usuarios.csv");
        if (!file.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 &&
                    Objects.equals(parts[0], usuario) &&
                    Objects.equals(parts[1], email) &&
                    Objects.equals(parts[2], tipoAcesso)) {
                    return true;
                }
            }
        } catch (Exception e) {
            lblMensagem.setText("Erro ao ler usuários: " + e.getMessage());
        }
        return false;
    }

    private boolean registerUser(String usuario, String email, String tipoAcesso) {
        File file = new File("data/usuarios.csv");
        // Verifica se já existe
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3 &&
                        Objects.equals(parts[0], usuario) &&
                        Objects.equals(parts[1], email)) {
                        return false; // Já existe
                    }
                }
            } catch (Exception e) {
                lblMensagem.setText("Erro ao ler usuários: " + e.getMessage());
                return false;
            }
        }
        // Adiciona novo usuário
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(usuario + "," + email + "," + tipoAcesso + "\n");
            return true;
        } catch (Exception e) {
            lblMensagem.setText("Erro ao salvar usuário: " + e.getMessage());
            return false;
        }
    }

    private boolean isEmailValido(String email) {
        // Regex simples para validar e-mail
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void showError(String message) {
        lblMensagem.setText(message);
        lblMensagem.setStyle("-fx-text-fill: red;");
    }
}