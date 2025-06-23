package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdicionarAnotacaoController {
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<String> cbHorta;
    @FXML private TextField txtAutor;
    @FXML private DatePicker datePicker;
    @FXML private VBox rootPane;

    private static final String ARQUIVO_CSV = "data/anotacoes.csv";
    private static final String ARQUIVO_HORTAS = "data/hortas.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        carregarHortas();
        datePicker.setValue(LocalDate.now());
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
                // Verifica se o arquivo existe e cria o cabeçalho se necessário
                Path path = Paths.get(ARQUIVO_CSV);
                if (!Files.exists(path)) {
                    Files.write(path, Collections.singletonList("titulo,descricao,horta,autor,data"));
                }
                
                // Adiciona a nova anotação
                List<String> linhas = new ArrayList<>(Files.readAllLines(path));
                linhas.add(formatarLinhaCsv());
                Files.write(path, linhas);
                
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Anotação salva com sucesso!");
                navegarParaListaAnotacoes();
                
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
        navegarParaListaAnotacoes();
    }

    private void navegarParaListaAnotacoes() {
        // Busca o StackPane contentArea subindo na hierarquia
        Parent parent = rootPane.getParent();
        while (parent != null && !(parent instanceof StackPane)) {
            parent = parent.getParent();
        }
        if (parent instanceof StackPane) {
            StackPane contentArea = (StackPane) parent;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/ListaNotes.fxml"));
                Parent listaNotesView = loader.load();
                contentArea.getChildren().setAll(listaNotesView);
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar para a lista de anotações");
                e.printStackTrace();
            }
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível encontrar o painel principal para exibir a lista de anotações.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 