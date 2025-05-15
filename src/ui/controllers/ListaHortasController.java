package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            // Implementar lógica de edição aqui
            mostrarAlerta("Editar", "Editando: " + selecionada.getNome());
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