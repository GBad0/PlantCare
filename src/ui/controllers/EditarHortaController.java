package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ui.models.Horta;
// import models.Horta;

import java.io.*;
import java.util.List;

public class EditarHortaController {
    @FXML private TextField txtNome;
    @FXML private ComboBox<String> cbPlantacao;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtResponsavel;
    @FXML private TextField txtDataPlantacao;
    @FXML private TextField txtLocalizacao;
    
    private Horta horta;
    private String arquivoCSV = "data/hortas.csv";
    private List<Horta> todasHortas;
    private int indiceHorta;

    public void setHorta(Horta horta, int indice, List<Horta> todasHortas) {
        this.horta = horta;
        this.indiceHorta = indice;
        this.todasHortas = todasHortas;
        
        // Preenche os campos com os dados da horta
        txtNome.setText(horta.getNome());
        cbPlantacao.getItems().addAll("Hortaliças", "Frutíferas", "Legumes", "Ervas", "Flores");
        cbPlantacao.setValue(horta.getPlantacao());
        txtQuantidade.setText(String.valueOf(horta.getQuantidade()));
        txtResponsavel.setText(horta.getResponsavel());
        txtDataPlantacao.setText(horta.getDataPlantacao());
        txtLocalizacao.setText(horta.getLocalizacao());
    }

    @FXML
    private void handleSalvar() {
        try {
            // Atualiza os dados da horta
            horta.setNome(txtNome.getText());
            horta.setPlantacao(cbPlantacao.getValue());
            horta.setQuantidade(Integer.parseInt(txtQuantidade.getText()));
            horta.setResponsavel(txtResponsavel.getText());
            horta.setDataPlantacao(txtDataPlantacao.getText());
            horta.setLocalizacao(txtLocalizacao.getText());
            
            // Atualiza a lista
            todasHortas.set(indiceHorta, horta);
            
            // Salva no CSV
            salvarNoCSV(todasHortas);
            
            // Fecha a janela
            Stage stage = (Stage) txtNome.getScene().getWindow();
            stage.close();
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Quantidade deve ser um número válido");
        }
    }

    private void salvarNoCSV(List<Horta> hortas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoCSV))) {
            // Escreve o cabeçalho
            writer.write("Nome,Tipo_Plantação,Quantidade,Responsável,Data_Plantação,Localização");
            writer.newLine();
            
            // Escreve cada horta
            for (Horta h : hortas) {
                writer.write(String.format("\"%s\",\"%s\",%d,\"%s\",%s,\"%s\"",
                        h.getNome(),
                        h.getPlantacao(),
                        h.getQuantidade(),
                        h.getResponsavel(),
                        h.getDataPlantacao(),
                        h.getLocalizacao()));
                writer.newLine();
            }
            
        } catch (IOException e) {
            mostrarAlerta("Erro", "Não foi possível salvar as alterações: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}