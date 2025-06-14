package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class ListaHortasController {
    @FXML private TableView<Horta> tabelaHortas;
    @FXML private TableColumn<Horta, String> colNome;
    @FXML private TableColumn<Horta, String> colPlantacao;
    @FXML private TableColumn<Horta, Integer> colQuantidade;
    @FXML private TableColumn<Horta, String> colResponsavel;
    @FXML private TableColumn<Horta, String> colData;
    @FXML private TableColumn<Horta, String> colLocalizacao;

    private static final String ARQUIVO_CSV = "data/hortas.csv";

    @FXML
    private void initialize() {
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPlantacao.setCellValueFactory(new PropertyValueFactory<>("plantacao"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("responsavel"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataPlantacao"));
        colLocalizacao.setCellValueFactory(new PropertyValueFactory<>("localizacao"));
    }

    private void carregarDados() {
        List<Horta> hortas = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_CSV))) {
            // Pular cabeçalho
            br.readLine();
            
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                
                // Remover aspas dos valores
                for (int i = 0; i < valores.length; i++) {
                    valores[i] = valores[i].replaceAll("^\"|\"$", "");
                }
                
                if (valores.length >= 6) {
                    Horta horta = new Horta(
                        valores[0], 
                        valores[1],
                        Integer.parseInt(valores[2]),
                        valores[3],
                        valores[4],
                        valores[5]
                    );
                    hortas.add(horta);
                }
            }
            
            tabelaHortas.getItems().setAll(hortas);
            
        } catch (IOException e) {
            mostrarAlerta("Erro", "Não foi possível carregar os dados: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Formato inválido no arquivo CSV");
        }
    }

    @FXML
    private void handleEditar() {
        Horta selecionada = tabelaHortas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            try {
                // Carrega a tela de edição
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/EditHortas.fxml"));
                Parent root = loader.load();
                
                // Obtém o controller e passa os dados
                EditarHortaController controller = loader.getController();
                int indice = tabelaHortas.getItems().indexOf(selecionada);
                controller.setHorta(selecionada, indice, new ArrayList<>(tabelaHortas.getItems()));
                
                // Cria e mostra a janela
                Stage stage = new Stage();
                stage.setTitle("Editar Horta");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                
                // Atualiza a tabela após edição
                carregarDados();
                
            } catch (IOException e) {
                mostrarAlerta("Erro", "Não foi possível abrir a tela de edição");
            }
        } else {
            mostrarAlerta("Aviso", "Selecione uma horta para editar");
        }
    }

    @FXML
    private void handleAtualizar() {
        carregarDados();
        mostrarAlerta("Sucesso", "Dados atualizados");
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}