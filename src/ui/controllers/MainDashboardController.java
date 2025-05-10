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
    private void showHortasList() {
        loadView("ListaHortas.fxml"); // Você precisará criar este arquivo depois
        //AQUI ADICIONAR A OPÇÃO DE  VISUALIZAR AS HORTAS E AO CLICAR EM ALGUMA EXIBIRA AS SUAS INFORMAÇÕES PLANTAÇÕES

    }

    @FXML
    private void showEditHorta() {
        loadView("ListaEditHortas.fxml"); // Você precisará criar este arquivo depois
        // EXIBIR "AS HORTAS" E UM BOTAO DE EDITAR/EXCLUIR E AO CLICAR NO EDITAR ELE ABRE PAGINA DE EDITAR A HORTA
        // Q TERA OS CAMPOS EDITAVEIS DA ORTA INCLINDO PLANTACAO 

        //EDITAR/DELETAR AS HORTAS EXISTENTES (INCLUINDO PLANDACAO(NOME,QUIANTIDADE))
    }

    @FXML
    private void showNewHorta() {
        loadView("NewHortas.fxml"); // Você precisará criar este arquivo depois
        //CRIARA UMA NOVA ORTA COM NOVA PLANTACAO TUDO DE NOVO
    }

    @FXML
    private void showAddNotes() {
        loadView("ListaAddNotes.fxml"); // Você precisará criar este arquivo depois
    }

    @FXML
    private void showNotes() {
        loadView("ListaNotes.fxml"); // Você precisará criar este arquivo depois
    }

    @FXML
    private void showColheita() {
        loadView("Colheita.fxml"); // Você precisará criar este arquivo depois
    }

    @FXML
    private void showPrevisaotempo() {
        loadView("PrevisaoTempo.fxml"); // Você precisará criar este arquivo depois
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
