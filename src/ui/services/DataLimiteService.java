package ui.services;

import ui.models.Horta;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataLimiteService {
    private static final String ARQUIVO_HORTAS = "data/hortas.csv";
    private static final String ARQUIVO_NOTIFICACOES = "data/notificacoes_enviadas.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void verificarDataLimite() {
        try {
            System.out.println("=== INICIANDO VERIFICAÇÃO DE DATA LIMITE ===");
            List<Horta> hortas = carregarHortas();
            LocalDate hoje = LocalDate.now();
            System.out.println("Data atual: " + hoje.format(DATE_FORMATTER));
            System.out.println("Total de hortas carregadas: " + hortas.size());
            
            for (Horta horta : hortas) {
                System.out.println("Verificando horta: " + horta.getNome());
                System.out.println("Data limite: " + horta.getDataLimiteColheita());
                
                if (deveEnviarNotificacao(horta, hoje)) {
                    System.out.println("ENVIANDO NOTIFICAÇÃO para: " + horta.getNome());
                    enviarNotificacaoDataLimite(horta);
                    registrarNotificacaoEnviada(horta.getNome(), hoje);
                } else {
                    System.out.println("NÃO enviando notificação para: " + horta.getNome());
                }
            }
            System.out.println("=== VERIFICACAO CONCLUIDA ===");
        } catch (Exception e) {
            System.err.println("Erro ao verificar data limite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<Horta> carregarHortas() {
        List<Horta> hortas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_HORTAS))) {
            br.readLine(); // Pular cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                
                // Remover aspas dos valores
                for (int i = 0; i < valores.length; i++) {
                    valores[i] = valores[i].replaceAll("^\"|\"$", "");
                }
                
                if (valores.length >= 6) {
                    String dataLimiteColheita = valores.length >= 7 ? valores[6] : "";
                    Horta horta = new Horta(
                        valores[0], 
                        valores[1],
                        Integer.parseInt(valores[2]),
                        valores[3],
                        valores[4],
                        valores[5],
                        dataLimiteColheita
                    );
                    hortas.add(horta);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar hortas: " + e.getMessage());
        }
        return hortas;
    }

    private static boolean deveEnviarNotificacao(Horta horta, LocalDate hoje) {
        System.out.println("  Verificando se deve enviar notificação para: " + horta.getNome());
        
        // Verificar se a horta tem data limite definida
        if (horta.getDataLimiteColheita() == null || horta.getDataLimiteColheita().isEmpty()) {
            System.out.println("  -> NÃO: Data limite vazia ou null");
            return false;
        }

        try {
            // Converter a data limite para LocalDate
            LocalDate dataLimite = LocalDate.parse(horta.getDataLimiteColheita(), DATE_FORMATTER);
            System.out.println("  -> Data limite convertida: " + dataLimite.format(DATE_FORMATTER));
            System.out.println("  -> Data atual: " + hoje.format(DATE_FORMATTER));
            System.out.println("  -> São iguais? " + hoje.isEqual(dataLimite));
            
            // Verificar se chegou o dia limite
            if (hoje.isEqual(dataLimite)) {
                boolean jaEnviada = notificacaoJaEnviada(horta.getNome(), hoje);
                System.out.println("  -> Notificação já enviada hoje? " + jaEnviada);
                return !jaEnviada;
            } else {
                System.out.println("  -> NÃO: Data atual diferente da data limite");
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar data limite da horta " + horta.getNome() + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    private static boolean notificacaoJaEnviada(String nomeHorta, LocalDate data) {
        try {
            File arquivo = new File(ARQUIVO_NOTIFICACOES);
            if (!arquivo.exists()) {
                return false;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
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
        } catch (IOException e) {
            System.err.println("Erro ao verificar notificações enviadas: " + e.getMessage());
        }
        return false;
    }

    private static void enviarNotificacaoDataLimite(Horta horta) {
        try {
            String emailResponsavel = horta.getResponsavel();
            
            if (emailResponsavel != null && !emailResponsavel.isEmpty()) {
                EmailService.sendDataLimiteEmail(emailResponsavel, horta);
                System.out.println("Notificação de data limite enviada para: " + emailResponsavel);
            }
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação de data limite: " + e.getMessage());
        }
    }

    private static void registrarNotificacaoEnviada(String nomeHorta, LocalDate data) {
        try {
            File arquivo = new File(ARQUIVO_NOTIFICACOES);
            // Garante que a pasta existe
            if (arquivo.getParentFile() != null) {
                arquivo.getParentFile().mkdirs();
            }
            boolean arquivoExiste = arquivo.exists();
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
                if (!arquivoExiste) {
                    bw.write("horta,data_notificacao\n");
                }
                bw.write(String.format("\"%s\",\"%s\"\n", nomeHorta, data.format(DATE_FORMATTER)));
            }
        } catch (IOException e) {
            System.err.println("Erro ao registrar notificação enviada: " + e.getMessage());
        }
    }
} 