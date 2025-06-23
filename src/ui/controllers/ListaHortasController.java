package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import ui.models.Horta;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ListaHortasController {
    @FXML private TableView<Horta> tabelaHortas;
    @FXML private TableColumn<Horta, String> colNome;
    @FXML private TableColumn<Horta, String> colPlantacao;
    @FXML private TableColumn<Horta, Integer> colQuantidade;
    @FXML private TableColumn<Horta, String> colResponsavel;
    @FXML private TableColumn<Horta, String> colData;
    @FXML private TableColumn<Horta, String> colLocalizacao;
    
    @FXML private TextField txtPesquisa;
    @FXML private ComboBox<String> cbFiltroPlantacao;

    private static final String ARQUIVO_CSV = "data/hortas.csv";
    private ObservableList<Horta> listaHortas = FXCollections.observableArrayList();
    private FilteredList<Horta> listaHortasFiltrada;

    @FXML
    private void initialize() {
        configurarColunas();
        configurarFiltros();
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

    private void configurarFiltros() {
        // Configurar ComboBox de filtro
        cbFiltroPlantacao.getItems().addAll(
            "Todas",
            "Hortaliças",
            "Ervas Aromáticas",
            "Frutíferas",
            "Flores Comestíveis"
        );
        cbFiltroPlantacao.setValue("Todas");
        
        // Configurar pesquisa e filtros
        listaHortasFiltrada = new FilteredList<>(listaHortas, p -> true);
        
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            listaHortasFiltrada.setPredicate(horta -> {
                if (newValue == null || newValue.isEmpty()) {
                    return filtrarPorTipo(horta);
                }
                
                String pesquisaLowerCase = newValue.toLowerCase();
                return (horta.getNome().toLowerCase().contains(pesquisaLowerCase) ||
                       horta.getResponsavel().toLowerCase().contains(pesquisaLowerCase) ||
                       horta.getLocalizacao().toLowerCase().contains(pesquisaLowerCase)) &&
                       filtrarPorTipo(horta);
            });
        });
        
        cbFiltroPlantacao.valueProperty().addListener((observable, oldValue, newValue) -> {
            listaHortasFiltrada.setPredicate(horta -> {
                if (txtPesquisa.getText().isEmpty()) {
                    return filtrarPorTipo(horta);
                }
                
                String pesquisaLowerCase = txtPesquisa.getText().toLowerCase();
                return (horta.getNome().toLowerCase().contains(pesquisaLowerCase) ||
                       horta.getResponsavel().toLowerCase().contains(pesquisaLowerCase) ||
                       horta.getLocalizacao().toLowerCase().contains(pesquisaLowerCase)) &&
                       filtrarPorTipo(horta);
            });
        });
        
        tabelaHortas.setItems(listaHortasFiltrada);
    }

    private boolean filtrarPorTipo(Horta horta) {
        String tipoSelecionado = cbFiltroPlantacao.getValue();
        return tipoSelecionado.equals("Todas") || horta.getPlantacao().equals(tipoSelecionado);
    }

    private void carregarDados() {
        listaHortas.clear();
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
                    listaHortas.add(horta);
                }
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os dados: " + e.getMessage());
        }
    }

    @FXML
    private void handleNovaHorta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/NewHortas.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Nova Horta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Atualiza a lista após fechar a janela
            carregarDados();
            
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de nova horta: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditar() {
        Horta selecionada = tabelaHortas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/EditHortas.fxml"));
                Parent root = loader.load();
                
                EditarHortaController controller = loader.getController();
                int indice = listaHortas.indexOf(selecionada);
                controller.setHorta(selecionada, indice, new ArrayList<>(listaHortas));
                
                Stage stage = new Stage();
                stage.setTitle("Editar Horta");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                
                carregarDados();
                
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de edição: " + e.getMessage());
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma horta para editar");
        }
    }

    @FXML
    private void handleExcluir() {
        Horta selecionada = tabelaHortas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText("Tem certeza que deseja excluir a horta \"" + selecionada.getNome() + "\"?");
            
            Optional<ButtonType> resultado = confirmacao.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                excluirHorta(selecionada);
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma horta para excluir");
        }
    }

    private void excluirHorta(Horta horta) {
        try {
            List<String> linhas = Files.readAllLines(Paths.get(ARQUIVO_CSV));
            List<String> novasLinhas = new ArrayList<>();
            novasLinhas.add(linhas.get(0)); // Adiciona o cabeçalho
            
            for (int i = 1; i < linhas.size(); i++) {
                String[] valores = linhas.get(i).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (valores.length > 0) {
                    String nomeHorta = valores[0].replaceAll("^\"|\"$", "");
                    if (!nomeHorta.equals(horta.getNome())) {
                        novasLinhas.add(linhas.get(i));
                    }
                }
            }
            
            Files.write(Paths.get(ARQUIVO_CSV), novasLinhas);
            carregarDados();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Horta excluída com sucesso!");
            
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível excluir a horta: " + e.getMessage());
        }
    }

    @FXML
    private void handleAtualizar() {
        carregarDados();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados atualizados com sucesso!");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}