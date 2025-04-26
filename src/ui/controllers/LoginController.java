package ui.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
        }
    }

    @FXML
    private void handleCadastro() {
        lblMensagem.setText("Redirecionando para cadastro...");
        // Implemente a navegação para a tela de cadastro depois
    }
}