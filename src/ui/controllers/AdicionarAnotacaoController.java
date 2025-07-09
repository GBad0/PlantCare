package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.concurrent.Task;
import javafx.application.Platform;
import ui.services.EmailService;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdicionarAnotacaoController {
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<String> cbHorta;
    @FXML private TextField txtAutor;
    @FXML private DatePicker datePicker;
    @FXML private VBox rootPane;
    @FXML private VBox progressContainer;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label progressLabel;

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
                
                // Obter dados para o email
                String nomeHorta = cbHorta.getValue();
                String autorAnotacao = txtAutor.getText().trim();
                String tituloAnotacao = txtTitulo.getText().trim();
                String descricaoAnotacao = txtDescricao.getText().trim();
                String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                
                // Enviar email em background
                enviarEmailNotificacao(nomeHorta, autorAnotacao, tituloAnotacao, descricaoAnotacao, dataHora);
                
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

    private void enviarEmailNotificacao(String nomeHorta, String autorAnotacao, String tituloAnotacao, String descricaoAnotacao, String dataHora) {
        // Mostrar indicador de progresso
        progressContainer.setVisible(true);
        
        Task<Void> emailTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Buscar email do responsável pela horta
                String emailResponsavel = buscarEmailResponsavel(nomeHorta);
                
                if (emailResponsavel != null && !emailResponsavel.isEmpty()) {
                    EmailService.sendAnotacaoEmail(emailResponsavel, nomeHorta, autorAnotacao, tituloAnotacao, descricaoAnotacao, dataHora);
                }
                
                return null;
            }
        };
        
        emailTask.setOnSucceeded(e -> {
            progressContainer.setVisible(false);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Anotação salva e notificação enviada com sucesso!");
            limparCampos();
            navegarParaListaAnotacoes();
        });
        
        emailTask.setOnFailed(e -> {
            progressContainer.setVisible(false);
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Anotação salva, mas houve um problema ao enviar a notificação por email.");
            limparCampos();
            navegarParaListaAnotacoes();
        });
        
        new Thread(emailTask).start();
    }
    
    private String buscarEmailResponsavel(String nomeHorta) {
        try {
            // Buscar o responsável pela horta
            String emailResponsavel = null;
            try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_HORTAS))) {
                br.readLine(); // Pular cabeçalho
                String linha;
                while ((linha = br.readLine()) != null) {
                    String[] valores = linha.split(",");
                    if (valores.length > 3) {
                        String hortaNome = valores[0].replaceAll("^\"|\"$", "");
                        if (hortaNome.equals(nomeHorta)) {
                            emailResponsavel = valores[3].replaceAll("^\"|\"$", "");
                            return emailResponsavel; // Retorna diretamente o email
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 