package ui.services;

import ui.models.Horta;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.FileInputStream;
import java.io.File;

public class EmailService {
    private static final String REMETENTE = "noreplyplantcare@gmail.com";
    private static final String SENHA = "osxk rcqn kthd wkke";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private static Properties carregarConfiguracoes() {
        Properties props = new Properties();
        
        // Configurações padrão
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        // Tentar carregar do arquivo de configuração
        try {
            File configFile = new File("email_config.properties");
            if (configFile.exists()) {
                System.out.println("Carregando configurações do arquivo email_config.properties");
                props.load(new FileInputStream(configFile));
            } else {
                System.out.println("Arquivo email_config.properties não encontrado, usando configurações padrão");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar configurações de email: " + e.getMessage());
            System.out.println("Usando configurações padrão");
        }
        
        return props;
    }

    public static void sendColheitaEmail(String destinatario, Horta horta, String usuarioColheu, String dataHora) {
        if (destinatario == null || destinatario.isEmpty()) return;
        String assunto = "[PlantCare] Colheita realizada na horta: " + horta.getNome();
        String corpo = String.format(
            "Olá,\n\nA horta '%s' foi colhida por %s em %s.\n\nQuantidade anterior de sementes: %d\n\nAtenciosamente,\nPlantCare",
            horta.getNome(), usuarioColheu, dataHora, horta.getQuantidade()
        );
        sendEmail(destinatario, assunto, corpo);
    }

    public static void sendAnotacaoEmail(String destinatario, String nomeHorta, String autorAnotacao, String tituloAnotacao, String descricaoAnotacao, String dataHora) {
        if (destinatario == null || destinatario.isEmpty()) return;
        String assunto = "[PlantCare] Nova anotação na horta: " + nomeHorta;
        String corpo = String.format(
            "Olá,\n\nUma nova anotação foi adicionada na horta '%s' por %s em %s.\n\nTítulo: %s\nDescrição: %s\n\nAtenciosamente,\nPlantCare",
            nomeHorta, autorAnotacao, dataHora, tituloAnotacao, descricaoAnotacao
        );
        sendEmail(destinatario, assunto, corpo);
    }

    public static void sendDataLimiteEmail(String destinatario, Horta horta) {
        System.out.println("=== ENVIANDO EMAIL DE DATA LIMITE ===");
        System.out.println("Destinatario: " + destinatario);
        System.out.println("Horta: " + horta.getNome());
        System.out.println("Data limite: " + horta.getDataLimiteColheita());
        
        if (destinatario == null || destinatario.isEmpty()) {
            System.out.println("ERRO: Destinatario null ou vazio");
            return;
        }
        
        String assunto = "[PlantCare] ATENCAO: Data limite de colheita atingida!";
        String corpo = String.format(
            "Ola,\n\nATENCAO: A horta '%s' atingiu sua data limite de colheita hoje (%s).\n\n" +
            "Detalhes da horta:\n" +
            "- Tipo de plantacao: %s\n" +
            "- Quantidade de sementes: %d\n" +
            "- Data de plantacao: %s\n" +
            "- Localizacao: %s\n\n" +
            "Por favor, realize a colheita o quanto antes para evitar perdas.\n\n" +
            "Atenciosamente,\nPlantCare",
            horta.getNome(), horta.getDataLimiteColheita(), horta.getPlantacao(), 
            horta.getQuantidade(), horta.getDataPlantacao(), horta.getLocalizacao()
        );
        
        System.out.println("Chamando sendEmail...");
        sendEmail(destinatario, assunto, corpo);
        System.out.println("Email de data limite processado");
    }

    private static void sendEmail(String destinatario, String assunto, String corpo) {
        System.out.println("=== SENDEMAIL ===");
        System.out.println("Remetente: " + REMETENTE);
        System.out.println("Senha configurada: " + (SENHA != null ? "SIM" : "NAO"));
        
        Properties props = carregarConfiguracoes();

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println("Autenticando com: " + REMETENTE);
                return new PasswordAuthentication(REMETENTE, SENHA);
            }
        });

        try {
            System.out.println("Criando mensagem...");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);
            message.setText(corpo);
            
            System.out.println("Enviando mensagem...");
            Transport.send(message);
            System.out.println("Email enviado com sucesso!");
        } catch (Exception e) {
            System.out.println("ERRO ao enviar email: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 