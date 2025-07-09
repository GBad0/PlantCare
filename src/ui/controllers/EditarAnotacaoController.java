package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import ui.models.Anotacao;

public class EditarAnotacaoController {
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<String> cbHorta;
    @FXML private TextField txtAutor;
    @FXML private DatePicker datePicker;

    private static final String ARQUIVO_CSV = "data/anotacoes.csv";
    private static final String ARQUIVO_HORTAS = "data/hortas.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private Anotacao anotacaoOriginal;

    @FXML
    private void initialize() {
        carregarHortas();
    }

    public void setAnotacao(Anotacao anotacao) {
        this.anotacaoOriginal = anotacao;
        
        txtTitulo.setText(anotacao.getTitulo());
        txtDescricao.setText(anotacao.getDescricao());
        cbHorta.setValue(anotacao.getHorta());
        txtAutor.setText(anotacao.getAutor());
        
        try {
            LocalDate data = LocalDate.parse(anotacao.getData(), formatter);
            datePicker.setValue(data);
        } catch (Exception e) {
            datePicker.setValue(LocalDate.now());
        }
    }

    private void carregarHortas() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_HORTAS))) {
            br.readLine(); // Pular cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(",");
                if (valores.length > 0) {
                    String nomeHorta = valores[0].replaceAll("^\"|\"$", "");
                    cbHorta.getItems().add(nomeHorta);
                }
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar as hortas: " + e.getMessage());
        }
    }

    @FXML
    private void handleSalvar() {
        if (validarCampos()) {
            try {
                List<String> linhas = Files.readAllLines(Paths.get(ARQUIVO_CSV));
                List<String> novasLinhas = new ArrayList<>();
                novasLinhas.add(linhas.get(0)); // Adiciona o cabeçalho
                
                boolean encontrou = false;
                for (int i = 1; i < linhas.size(); i++) {
                    String[] valores = linhas.get(i).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    if (valores.length > 0) {
                        String tituloAnotacao = valores[0].replaceAll("^\"|\"$", "");
                        String hortaAnotacao = valores[2].replaceAll("^\"|\"$", "");
                        
                        if (tituloAnotacao.equals(anotacaoOriginal.getTitulo()) && 
                            hortaAnotacao.equals(anotacaoOriginal.getHorta())) {
                            // Substitui a linha antiga pela nova
                            novasLinhas.add(formatarLinhaCsv());
                            encontrou = true;
                        } else {
                            novasLinhas.add(linhas.get(i));
                        }
                    }
                }
                
                if (!encontrou) {
                    novasLinhas.add(formatarLinhaCsv());
                }
                
                Files.write(Paths.get(ARQUIVO_CSV), novasLinhas);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Anotação atualizada com sucesso!");
                limparCampos();
                fecharJanela();
                
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível salvar a anotação: " + e.getMessage());
            }
        }
    }

    private String formatarLinhaCsv() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
            txtTitulo.getText().trim(),
            txtDescricao.getText().trim(),
            cbHorta.getValue(),
            txtAutor.getText().trim(),
            datePicker.getValue().format(formatter)
        );
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "O título é obrigatório!");
            return false;
        }
        
        if (txtDescricao.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "A descrição é obrigatória!");
            return false;
        }
        
        if (cbHorta.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma horta!");
            return false;
        }
        
        if (txtAutor.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "O autor é obrigatório!");
            return false;
        }
        
        if (datePicker.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma data!");
            return false;
        }
        
        return true;
    }

    @FXML
    private void handleCancelar() {
        fecharJanela();
    }

    private void limparCampos() {
        txtTitulo.clear();
        txtDescricao.clear();
        cbHorta.getSelectionModel().clearSelection();
        txtAutor.clear();
        datePicker.setValue(LocalDate.now());
        
        // Focar no primeiro campo para facilitar nova entrada
        txtTitulo.requestFocus();
    }

    private void fecharJanela() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 