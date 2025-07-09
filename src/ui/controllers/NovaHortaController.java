package ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import ui.services.DataLimiteService;
import ui.models.Horta;
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
    @FXML private ComboBox<String> cbResponsavel;
    @FXML private DatePicker dpDataPlantacao;
    @FXML private TextField txtLocalizacao;
    @FXML private DatePicker dpDataLimiteColheita;

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
        carregarResponsaveis();
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
                    writer.println("Nome,Tipo_Plantação,Quantidade,Responsável,Data_Plantação,Localização,Data_Limite_Colheita");
                }
            }
        } catch (IOException e) {
            showAlert("Erro", "Falha ao inicializar arquivo de dados: " + e.getMessage());
        }
    }

    private void carregarResponsaveis() {
        List<String> responsaveis = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/usuarios.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && "Responsável".equalsIgnoreCase(parts[2].replaceAll("[\"\n\r]", "").trim())) {
                    responsaveis.add(parts[0] + " (" + parts[1] + ")"); // Exibe nome (email)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cbResponsavel.setItems(FXCollections.observableArrayList(responsaveis));
    }

    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(ARQUIVO_CSV, true))) {
            out.println(formatarLinhaCSV());
            showAlert("Sucesso", "Dados salvos em: " + new File(ARQUIVO_CSV).getAbsolutePath());
            
            // Verificar se a data limite é hoje e enviar notificação imediatamente
            verificarDataLimiteNovaHorta();
            
            // Limpar todos os campos após salvar com sucesso
            limparCampos();
            
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

        // Validar se a data limite de colheita é posterior à data de plantação
        if (dpDataLimiteColheita.getValue() != null && dpDataPlantacao.getValue() != null) {
            if (dpDataLimiteColheita.getValue().isBefore(dpDataPlantacao.getValue())) {
                showAlert("Aviso", "A data limite de colheita deve ser posterior à data de plantação!");
                return false;
            }
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
        String responsavelCombo = cbResponsavel.getValue();
        String responsavelEmail = responsavelCombo;
        if (responsavelCombo != null && responsavelCombo.contains("(") && responsavelCombo.contains(")")) {
            responsavelEmail = responsavelCombo.substring(responsavelCombo.indexOf('(') + 1, responsavelCombo.indexOf(')'));
        }
        
        String dataLimiteColheita = "";
        if (dpDataLimiteColheita.getValue() != null) {
            dataLimiteColheita = dpDataLimiteColheita.getValue().format(DATE_FORMATTER);
        }
        
        return String.format("\"%s\",\"%s\",%s,\"%s\",%s,\"%s\",\"%s\"",
            txtNome.getText(),
            cbPlantacao.getValue(),
            txtQuantidade.getText(),
            responsavelEmail,
            dpDataPlantacao.getValue().format(DATE_FORMATTER),
            txtLocalizacao.getText(),
            dataLimiteColheita);
    }


    private void verificarDataLimiteNovaHorta() {
        // Verificar se a data limite é hoje
        if (dpDataLimiteColheita.getValue() != null) {
            String dataLimite = dpDataLimiteColheita.getValue().format(DATE_FORMATTER);
            String dataHoje = java.time.LocalDate.now().format(DATE_FORMATTER);
            
            System.out.println("=== VERIFICANDO DATA LIMITE DA NOVA HORTA ===");
            System.out.println("Data limite: " + dataLimite);
            System.out.println("Data hoje: " + dataHoje);
            
            if (dataLimite.equals(dataHoje)) {
                System.out.println("Data limite é hoje! Enviando notificação...");
                
                // Criar a horta para verificação
                String responsavelCombo = cbResponsavel.getValue();
                String responsavelEmail = responsavelCombo;
                if (responsavelCombo != null && responsavelCombo.contains("(") && responsavelCombo.contains(")")) {
                    responsavelEmail = responsavelCombo.substring(responsavelCombo.indexOf('(') + 1, responsavelCombo.indexOf(')'));
                }
                
                Horta novaHorta = new Horta(
                    txtNome.getText(),
                    cbPlantacao.getValue(),
                    Integer.parseInt(txtQuantidade.getText()),
                    responsavelEmail,
                    dpDataPlantacao.getValue().format(DATE_FORMATTER),
                    txtLocalizacao.getText(),
                    dataLimite
                );
                
                // Executar verificação específica da nova horta em background
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        System.out.println("Verificando horta recém-criada: " + novaHorta.getNome());
                        
                        // Verificar se deve enviar notificação para esta horta específica
                        java.time.LocalDate hoje = java.time.LocalDate.now();
                        if (deveEnviarNotificacaoHorta(novaHorta, hoje)) {
                            System.out.println("Enviando notificação para horta recém-criada...");
                            enviarNotificacaoHorta(novaHorta);
                            registrarNotificacaoHorta(novaHorta.getNome(), hoje);
                        }
                        return null;
                    }
                };
                
                task.setOnSucceeded(e -> {
                    System.out.println("Verificação de data limite concluída para nova horta");
                });
                
                task.setOnFailed(e -> {
                    System.err.println("Erro na verificação de data limite: " + task.getException().getMessage());
                });
                
                new Thread(task).start();
            } else {
                System.out.println("Data limite não é hoje. Não enviando notificação.");
            }
        }
    }
    
    // Métodos auxiliares para verificação específica da horta
    private boolean deveEnviarNotificacaoHorta(Horta horta, java.time.LocalDate hoje) {
        System.out.println("  Verificando se deve enviar notificação para: " + horta.getNome());
        
        if (horta.getDataLimiteColheita() == null || horta.getDataLimiteColheita().isEmpty()) {
            System.out.println("  -> NÃO: Data limite vazia ou null");
            return false;
        }

        try {
            java.time.LocalDate dataLimite = java.time.LocalDate.parse(horta.getDataLimiteColheita(), DATE_FORMATTER);
            System.out.println("  -> Data limite convertida: " + dataLimite.format(DATE_FORMATTER));
            System.out.println("  -> Data atual: " + hoje.format(DATE_FORMATTER));
            System.out.println("  -> São iguais? " + hoje.isEqual(dataLimite));
            
            if (hoje.isEqual(dataLimite)) {
                boolean jaEnviada = notificacaoJaEnviadaHorta(horta.getNome(), hoje);
                System.out.println("  -> Notificação já enviada hoje? " + jaEnviada);
                return !jaEnviada;
            } else {
                System.out.println("  -> NÃO: Data atual diferente da data limite");
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar data limite da horta " + horta.getNome() + ": " + e.getMessage());
            System.err.println("Data limite original: '" + horta.getDataLimiteColheita() + "'");
            e.printStackTrace();
        }
        
        return false;
    }
    
    private boolean notificacaoJaEnviadaHorta(String nomeHorta, java.time.LocalDate data) {
        try {
            java.io.File arquivo = new java.io.File("data/notificacoes_enviadas.csv");
            if (!arquivo.exists()) {
                return false;
            }

            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(arquivo))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    String[] valores = linha.split(",");
                    if (valores.length >= 2) {
                        String horta = valores[0].replaceAll("^\"|\"$", "");
                        String dataNotificacao = valores[1].replaceAll("^\"|\"$", "");
                        
                        if (horta.equals(nomeHorta) && dataNotificacao.equals(data.format(DATE_FORMATTER))) {
                            return true;
                        }
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("Erro ao verificar notificações enviadas: " + e.getMessage());
        }
        return false;
    }
    
    private void enviarNotificacaoHorta(Horta horta) {
        try {
            String emailResponsavel = horta.getResponsavel();
            
            System.out.println("=== ENVIANDO NOTIFICAÇÃO DE DATA LIMITE ===");
            System.out.println("Horta: " + horta.getNome());
            System.out.println("Email responsável: " + emailResponsavel);
            System.out.println("Data limite: " + horta.getDataLimiteColheita());
            
            if (emailResponsavel != null && !emailResponsavel.isEmpty()) {
                System.out.println("Chamando EmailService.sendDataLimiteEmail...");
                ui.services.EmailService.sendDataLimiteEmail(emailResponsavel, horta);
                System.out.println("Notificação de data limite enviada para: " + emailResponsavel);
            } else {
                System.err.println("ERRO: Email do responsável está vazio ou null");
                System.err.println("Email responsável: '" + emailResponsavel + "'");
            }
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação de data limite: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void registrarNotificacaoHorta(String nomeHorta, java.time.LocalDate data) {
        try {
            java.io.File arquivo = new java.io.File("data/notificacoes_enviadas.csv");
            if (arquivo.getParentFile() != null) {
                arquivo.getParentFile().mkdirs();
            }
            boolean arquivoExiste = arquivo.exists();
            
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(arquivo, true))) {
                if (!arquivoExiste) {
                    bw.write("horta,data_notificacao\n");
                }
                bw.write(String.format("\"%s\",\"%s\"\n", nomeHorta, data.format(DATE_FORMATTER)));
            }
        } catch (java.io.IOException e) {
            System.err.println("Erro ao registrar notificação enviada: " + e.getMessage());
        }
    }

    private void limparCampos() {
        txtNome.clear();
        cbPlantacao.getSelectionModel().selectFirst();
        txtQuantidade.clear();
        cbResponsavel.getSelectionModel().clearSelection();
        dpDataPlantacao.setValue(null);
        txtLocalizacao.clear();
        dpDataLimiteColheita.setValue(null);
        
        // Focar no primeiro campo para facilitar nova entrada
        txtNome.requestFocus();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}