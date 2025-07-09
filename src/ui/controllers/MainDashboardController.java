package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.concurrent.Task;
import ui.services.DataLimiteService;

import java.io.IOException;

public class MainDashboardController {
    @FXML private StackPane contentArea;
    @FXML private Button btnAdicionarHorta;
    private String tipoAcessoUsuario = "";

    public void setTipoAcessoUsuario(String tipo) {
        this.tipoAcessoUsuario = tipo;
        if (btnAdicionarHorta != null) {
            btnAdicionarHorta.setVisible(!"Ajudante".equalsIgnoreCase(tipo));
        }
    }

    @FXML
    private void initialize() {
        if (contentArea == null) {
            System.err.println("ERRO: contentArea não foi injetado pelo FXMLLoader");
        } else {
            System.out.println("INFO: contentArea foi inicializado com sucesso");
        }
        // Esconde o botão se o tipo já estiver setado antes do initialize
        if (btnAdicionarHorta != null && "Ajudante".equalsIgnoreCase(tipoAcessoUsuario)) {
            btnAdicionarHorta.setVisible(false);
        }
        
        // Teste simples para verificar se o sistema está funcionando
        System.out.println("=== TESTE SIMPLES DO SISTEMA ===");
        System.out.println("Data atual do sistema: " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        // Verificar data limite de colheita em background
        verificarDataLimiteEmBackground();
    }

    // Métodos para trocar as telas
    @FXML
    private void showHortasList() {
        loadView("ListaHortas.fxml");
    }

    @FXML
    private void showEditHorta() {
        loadView("EditHortas.fxml");
    }

    @FXML
    private void showNewHorta() {
        loadView("NewHortas.fxml");
    }

    @FXML
    private void showAddNotes() {
        loadView("ListaAddNotes.fxml");
    }

    @FXML
    private void showNotes() {
        loadView("ListaNotes.fxml");
    }

    @FXML
    private void showColheita() {
        loadView("Colheita.fxml");
    }

    @FXML
    private void showPrevisaotempo() {
        loadView("PrevisaoTempo.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/ui/views/Login.fxml"));
            Scene scene = contentArea.getScene();
            if (scene != null) {
                scene.setRoot(loginView);
            } else {
                showAlert("Erro", "Scene não encontrada");
            }
        } catch (IOException e) {
            showAlert("Erro", "Não foi possível fazer logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) {
        if (contentArea == null) {
            System.err.println("ERRO: contentArea é null ao tentar carregar " + fxmlFile);
            showAlert("Erro", "contentArea não foi inicializado corretamente");
            return;
        }

        try {
            System.out.println("INFO: Tentando carregar " + fxmlFile);
            String resourcePath = "/ui/views/" + fxmlFile;
            System.out.println("INFO: Caminho do recurso: " + resourcePath);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            if (loader.getLocation() == null) {
                System.err.println("ERRO: Não foi possível encontrar o arquivo " + resourcePath);
                showAlert("Erro", "Arquivo não encontrado: " + fxmlFile);
                return;
            }

            Parent view = loader.load();
            System.out.println("INFO: View carregada com sucesso");
            contentArea.getChildren().setAll(view);
            System.out.println("INFO: View adicionada ao contentArea");
            
        } catch (IOException e) {
            System.err.println("ERRO ao carregar " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
            showAlert("Erro", "Não foi possível carregar a view " + fxmlFile + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERRO inesperado ao carregar " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
            showAlert("Erro", "Erro inesperado ao carregar a view " + fxmlFile + ": " + e.getMessage());
        }
    }

    private void verificarDataLimiteEmBackground() {
        System.out.println("Iniciando verificação de data limite...");
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Executando verificação em background...");
                // Aguarda um pouco para não interferir no carregamento da interface
                Thread.sleep(1000);
                DataLimiteService.verificarDataLimite();
                return null;
            }
        };
        
        task.setOnSucceeded(e -> {
            System.out.println("Verificação de data limite concluída com sucesso");
        });
        
        task.setOnFailed(e -> {
            System.err.println("Erro na verificação de data limite: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });
        
        new Thread(task).start();
    }

    private void showAlert(String title, String message) {
        System.err.println("ALERTA: " + title + " - " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
