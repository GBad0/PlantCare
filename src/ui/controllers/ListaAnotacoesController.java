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

import java.io.*;
import java.nio.file.*;
import java.util.*;
import ui.models.Anotacao;

public class ListaAnotacoesController {
    @FXML private TableView<Anotacao> tabelaAnotacoes;
    @FXML private TableColumn<Anotacao, String> colTitulo;
    @FXML private TableColumn<Anotacao, String> colDescricao;
    @FXML private TableColumn<Anotacao, String> colHorta;
    @FXML private TableColumn<Anotacao, String> colAutor;
    @FXML private TableColumn<Anotacao, String> colData;
    
    @FXML private TextField txtPesquisa;
    @FXML private ComboBox<String> cbFiltroHorta;

    private static final String ARQUIVO_CSV = "data/anotacoes.csv";
    private static final String ARQUIVO_HORTAS = "data/hortas.csv";
    private ObservableList<Anotacao> listaAnotacoes = FXCollections.observableArrayList();
    private FilteredList<Anotacao> listaAnotacoesFiltrada;

    @FXML
    private void initialize() {
        configurarColunas();
        configurarFiltros();
        carregarDados();
    }

    private void configurarColunas() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colHorta.setCellValueFactory(new PropertyValueFactory<>("horta"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
    }

    private void configurarFiltros() {
        // Carregar hortas para o ComboBox
        carregarHortasParaFiltro();
        
        // Configurar pesquisa e filtros
        listaAnotacoesFiltrada = new FilteredList<>(listaAnotacoes, p -> true);
        
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            listaAnotacoesFiltrada.setPredicate(anotacao -> {
                if (newValue == null || newValue.isEmpty()) {
                    return filtrarPorHorta(anotacao);
                }
                
                String pesquisaLowerCase = newValue.toLowerCase();
                return (anotacao.getTitulo().toLowerCase().contains(pesquisaLowerCase) ||
                       anotacao.getDescricao().toLowerCase().contains(pesquisaLowerCase) ||
                       anotacao.getAutor().toLowerCase().contains(pesquisaLowerCase)) &&
                       filtrarPorHorta(anotacao);
            });
        });
        
        cbFiltroHorta.valueProperty().addListener((observable, oldValue, newValue) -> {
            listaAnotacoesFiltrada.setPredicate(anotacao -> {
                if (txtPesquisa.getText().isEmpty()) {
                    return filtrarPorHorta(anotacao);
                }
                
                String pesquisaLowerCase = txtPesquisa.getText().toLowerCase();
                return (anotacao.getTitulo().toLowerCase().contains(pesquisaLowerCase) ||
                       anotacao.getDescricao().toLowerCase().contains(pesquisaLowerCase) ||
                       anotacao.getAutor().toLowerCase().contains(pesquisaLowerCase)) &&
                       filtrarPorHorta(anotacao);
            });
        });
        
        tabelaAnotacoes.setItems(listaAnotacoesFiltrada);
    }

    private void carregarHortasParaFiltro() {
        Set<String> hortas = new HashSet<>();
        hortas.add("Todas");
        
        // Verificar se o arquivo de hortas existe
        File arquivoHortas = new File(ARQUIVO_HORTAS);
        if (!arquivoHortas.exists()) {
            try {
                // Criar diretório se não existir
                arquivoHortas.getParentFile().mkdirs();
                
                // Criar arquivo com cabeçalho básico
                try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_HORTAS))) {
                    writer.println("Nome,Localização,Data de Criação");
                }
            } catch (IOException e) {
                // Se não conseguir criar, apenas continuar com a lista vazia
            }
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_HORTAS))) {
            br.readLine(); // Pular cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(",");
                if (valores.length > 0) {
                    hortas.add(valores[0].replaceAll("^\"|\"$", ""));
                }
            }
        } catch (IOException e) {
            // Se não conseguir ler o arquivo, apenas continuar com a lista vazia
            // Não mostrar erro para não interromper a funcionalidade
        }
        
        cbFiltroHorta.getItems().addAll(hortas);
        cbFiltroHorta.setValue("Todas");
    }

    private boolean filtrarPorHorta(Anotacao anotacao) {
        String hortaSelecionada = cbFiltroHorta.getValue();
        return hortaSelecionada.equals("Todas") || anotacao.getHorta().equals(hortaSelecionada);
    }

    private void carregarDados() {
        listaAnotacoes.clear();
        
        // Verificar se o arquivo existe, se não, criar com cabeçalho
        File arquivoAnotacoes = new File(ARQUIVO_CSV);
        if (!arquivoAnotacoes.exists()) {
            try {
                // Criar diretório se não existir
                arquivoAnotacoes.getParentFile().mkdirs();
                
                // Criar arquivo com cabeçalho
                try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_CSV))) {
                    writer.println("Título,Descrição,Horta,Autor,Data");
                }
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não foi possível criar o arquivo de anotações. A lista estará vazia.");
                return;
            }
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_CSV))) {
            String primeiraLinha = br.readLine();
            if (primeiraLinha == null) {
                // Arquivo vazio, adicionar cabeçalho
                try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_CSV))) {
                    writer.println("Título,Descrição,Horta,Autor,Data");
                }
                return;
            }
            
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                
                // Remover aspas dos valores
                for (int i = 0; i < valores.length; i++) {
                    valores[i] = valores[i].replaceAll("^\"|\"$", "");
                }
                
                if (valores.length >= 5) {
                    Anotacao anotacao = new Anotacao(
                        valores[0],
                        valores[1],
                        valores[2],
                        valores[3],
                        valores[4]
                    );
                    listaAnotacoes.add(anotacao);
                }
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não foi possível carregar os dados: " + e.getMessage() + "\nA lista estará vazia.");
        }
    }

    @FXML
    private void handleNovaAnotacao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/ListaAddNotes.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Nova Anotação");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            carregarDados();
            
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de nova anotação: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditar() {
        Anotacao selecionada = tabelaAnotacoes.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/ListaEditNotes.fxml"));
                Parent root = loader.load();
                
                EditarAnotacaoController controller = loader.getController();
                controller.setAnotacao(selecionada);
                
                Stage stage = new Stage();
                stage.setTitle("Editar Anotação");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                
                carregarDados();
                
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de edição: " + e.getMessage());
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma anotação para editar");
        }
    }

    @FXML
    private void handleExcluir() {
        Anotacao selecionada = tabelaAnotacoes.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText("Tem certeza que deseja excluir a anotação \"" + selecionada.getTitulo() + "\"?");
            
            Optional<ButtonType> resultado = confirmacao.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                excluirAnotacao(selecionada);
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma anotação para excluir");
        }
    }

    private void excluirAnotacao(Anotacao anotacao) {
        try {
            List<String> linhas = Files.readAllLines(Paths.get(ARQUIVO_CSV));
            List<String> novasLinhas = new ArrayList<>();
            novasLinhas.add(linhas.get(0)); // Adiciona o cabeçalho
            
            for (int i = 1; i < linhas.size(); i++) {
                String[] valores = linhas.get(i).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (valores.length > 0) {
                    String tituloAnotacao = valores[0].replaceAll("^\"|\"$", "");
                    String hortaAnotacao = valores[2].replaceAll("^\"|\"$", "");
                    if (!tituloAnotacao.equals(anotacao.getTitulo()) || !hortaAnotacao.equals(anotacao.getHorta())) {
                        novasLinhas.add(linhas.get(i));
                    }
                }
            }
            
            Files.write(Paths.get(ARQUIVO_CSV), novasLinhas);
            carregarDados();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Anotação excluída com sucesso!");
            
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível excluir a anotação: " + e.getMessage());
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