package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NovaHortaController {
    @FXML private TextField txtNome;
    @FXML private ComboBox<String> cbPlantacao;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtResponsavel;
    @FXML private DatePicker dpDataPlantacao;
    @FXML private TextField txtLocalizacao;

    private static final String DATA_DIR = "data";
    private static final String ARQUIVO_CSV = DATA_DIR + "/hortas.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @FXML
    private void initialize() {
        // Inicializa o ComboBox
        cbPlantacao.getItems().addAll(
            "Hortaliças",
            "Ervas Aromáticas",
            "Frutíferas",
            "Flores Comestíveis"
        );
        cbPlantacao.getSelectionModel().selectFirst();
        
        // Verifica se o arquivo CSV existe, se não, cria com cabeçalho
        criarEstruturaArquivos();
    }
    
    private void criarArquivoSeNaoExistir() {
        File arquivo = new File(ARQUIVO_CSV);
        if (!arquivo.exists()) {
            try (PrintWriter writer = new PrintWriter(arquivo)) {
                writer.println("Nome,Tipo_Plantação,Quantidade,Responsável,Data_Plantação,Localização");
            } catch (FileNotFoundException e) {
                showAlert("Erro", "Não foi possível criar o arquivo de dados");
            }
        }
    }

    private void criarEstruturaArquivos() {
        try {
            // Cria o diretório data se não existir
            Files.createDirectories(Paths.get(DATA_DIR));
            
            // Cria o arquivo CSV com cabeçalho se não existir
            if (!Files.exists(Paths.get(ARQUIVO_CSV))) {
                try (PrintWriter writer = new PrintWriter(ARQUIVO_CSV)) {
                    writer.println("Nome,Tipo_Plantação,Quantidade,Responsável,Data_Plantação,Localização");
                }
            }
        } catch (IOException e) {
            showAlert("Erro", "Falha ao inicializar arquivo de dados: " + e.getMessage());
        }
    }

    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(ARQUIVO_CSV, true))) {
            out.println(formatarLinhaCSV());
            showAlert("Sucesso", "Dados salvos em: " + new File(ARQUIVO_CSV).getAbsolutePath());
        } catch (IOException e) {
            showAlert("Erro", "Falha ao salvar: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().isEmpty() || cbPlantacao.getValue() == null || 
            txtQuantidade.getText().isEmpty() || dpDataPlantacao.getValue() == null) {
            showAlert("Aviso", "Preencha todos os campos obrigatórios!");
            return false;
        }

        try {
            Integer.parseInt(txtQuantidade.getText());
            return true;
        } catch (NumberFormatException e) {
            showAlert("Erro", "Quantidade deve ser um número");
            return false;
        }
    }

    private String formatarLinhaCSV() {
        return String.format("\"%s\",\"%s\",%s,\"%s\",%s,\"%s\"",
            txtNome.getText(),
            cbPlantacao.getValue(),
            txtQuantidade.getText(),
            txtResponsavel.getText(),
            dpDataPlantacao.getValue().format(DATE_FORMATTER),
            txtLocalizacao.getText());
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}