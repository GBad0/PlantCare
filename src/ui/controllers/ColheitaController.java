package ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ui.models.Horta;
import ui.services.EmailService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.application.Platform;

public class ColheitaController {
    @FXML private TableView<Horta> tabelaHortas;
    @FXML private TableColumn<Horta, String> colNome;
    @FXML private TableColumn<Horta, String> colPlantacao;
    @FXML private TableColumn<Horta, Integer> colQuantidade;
    @FXML private TableColumn<Horta, String> colResponsavel;
    @FXML private TableColumn<Horta, String> colData;
    @FXML private TableColumn<Horta, String> colLocalizacao;
    @FXML private Button btnColher;
    @FXML private Button btnHistorico;
    @FXML private ProgressIndicator loadingIndicator;

    private ObservableList<Horta> hortas = FXCollections.observableArrayList();
    private static final String HORTAS_CSV = "data/hortas.csv";
    private static final String COLHEITAS_CSV = "data/colheitas.csv";

    @FXML
    private void initialize() {
        colNome.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));
        colPlantacao.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPlantacao()));
        colQuantidade.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantidade()).asObject());
        colResponsavel.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getResponsavel()));
        colData.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataPlantacao()));
        colLocalizacao.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getLocalizacao()));
        carregarHortas();
    }

    private void carregarHortas() {
        hortas.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(HORTAS_CSV))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    int quantidade = Integer.parseInt(parts[2].replaceAll("\\D", ""));
                    if (quantidade > 0) {
                        String dataLimiteColheita = parts.length >= 7 ? parts[6].replaceAll("\"", "") : "";
                        hortas.add(new Horta(
                            parts[0].replaceAll("\"", ""),
                            parts[1].replaceAll("\"", ""),
                            quantidade,
                            parts[3].replaceAll("\"", ""),
                            parts[4].replaceAll("\"", ""),
                            parts[5].replaceAll("\"", ""),
                            dataLimiteColheita
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabelaHortas.setItems(hortas);
    }

    @FXML
    private void handleColher() {
        Horta selecionada = tabelaHortas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Selecione uma horta para colher.");
            return;
        }
        loadingIndicator.setVisible(true);
        btnColher.setDisable(true);
        btnHistorico.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                salvarColheita(selecionada);
                atualizarQuantidadeHorta(selecionada.getNome(), 0);
                String responsavelEmail = selecionada.getResponsavel();
                if (responsavelEmail != null && responsavelEmail.contains("(") && responsavelEmail.contains(")")) {
                    responsavelEmail = responsavelEmail.substring(responsavelEmail.indexOf('(') + 1, responsavelEmail.indexOf(')'));
                }
                String usuarioColheu = System.getProperty("user.name");
                String dataHora = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                EmailService.sendColheitaEmail(responsavelEmail, selecionada, usuarioColheu, dataHora);
                return null;
            }
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    carregarHortas();
                    loadingIndicator.setVisible(false);
                    btnColher.setDisable(false);
                    btnHistorico.setDisable(false);
                    mostrarAlerta("Colheita realizada!");
                });
            }
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    btnColher.setDisable(false);
                    btnHistorico.setDisable(false);
                    mostrarAlerta("Erro ao realizar colheita!");
                });
            }
        };
        new Thread(task).start();
    }

    private void salvarColheita(Horta horta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COLHEITAS_CSV, true))) {
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            bw.write(horta.getNome() + "," + horta.getPlantacao() + "," + horta.getQuantidade() + "," + dataHora + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void atualizarQuantidadeHorta(String nome, int novaQuantidade) {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(HORTAS_CSV))) {
            String header = br.readLine();
            linhas.add(header);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].replaceAll("\"", "").equals(nome)) {
                    parts[2] = String.valueOf(novaQuantidade);
                    linhas.add(String.join(",", parts));
                } else {
                    linhas.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(HORTAS_CSV))) {
            for (String l : linhas) {
                bw.write(l + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHistorico() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Histórico de Colheitas");
        alert.setHeaderText("Colheitas realizadas:");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(COLHEITAS_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            sb.append("Nenhum histórico encontrado.");
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
} 