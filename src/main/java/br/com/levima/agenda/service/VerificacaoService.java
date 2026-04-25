package br.com.levima.agenda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class VerificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(VerificacaoService.class);

    @Value("${vonage.api-key:}")
    private String vonageApiKey;

    @Value("${vonage.api-secret:}")
    private String vonageApiSecret;

    @Value("${vonage.from-number:MeAgenda}")
    private String vonageFromNumber;

    public boolean enviarCodigoVerificacao(String email, String celular, String codigo) {
        // MODO DESENVOLVIMENTO: Apenas imprimir código no console
        // Remover temporariamente o envio real de SMS via Vonage para evitar gastar créditos
        logger.info("🔧 MODO DESENVOLVIMENTO - Envio de SMS desativado");
        return enviarCodigoVerificacaoTeste(email, celular, codigo);

        /* CÓDIGO COMENTADO PARA PRODUÇÃO - DESCOMENTE QUANDO ESTIVER PRONTO
        // Log das credenciais (sem mostrar valores sensíveis)
        logger.info("Tentando enviar SMS...");
        logger.info("API Key configurada: {}", !vonageApiKey.isEmpty());
        logger.info("API Secret configurada: {}", !vonageApiSecret.isEmpty());
        
        // Se não tiver credenciais Vonage, usar modo teste
        if (vonageApiKey.isEmpty() || vonageApiSecret.isEmpty()) {
            logger.warn("Credenciais Vonage não configuradas! Usando modo teste.");
            return enviarCodigoVerificacaoTeste(email, celular, codigo);
        }

        try {
            // Formatar número - remover caracteres especiais
            String numeroBrasil = "+55" + celular.replaceAll("[^0-9]", "");
            logger.info("Número formatado: {}", numeroBrasil);

            // Preparar mensagem SMS
            String mensagem = "🔐 Seu código de verificação é: " + codigo + 
                            "\n⏱️ Validade: 10 minutos\n" +
                            "⚠️ Não compartilhe este código com ninguém.";
            
            logger.info("Remetente: {}", vonageFromNumber);
            logger.info("Destinatário: {}", numeroBrasil);

            // Construir URL da API Vonage
            String urlParams = String.format(
                "api_key=%s&api_secret=%s&to=%s&from=%s&text=%s",
                URLEncoder.encode(vonageApiKey, StandardCharsets.UTF_8),
                URLEncoder.encode(vonageApiSecret, StandardCharsets.UTF_8),
                URLEncoder.encode(numeroBrasil, StandardCharsets.UTF_8),
                URLEncoder.encode(vonageFromNumber, StandardCharsets.UTF_8),
                URLEncoder.encode(mensagem, StandardCharsets.UTF_8)
            );

            String url = "https://rest.nexmo.com/sms/json?" + urlParams;
            logger.info("Enviando para URL: https://rest.nexmo.com/sms/json?...");

            // Criar requisição HTTP
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .GET()
                    .header("User-Agent", "MeAgenda/1.0")
                    .build();

            // Enviar requisição
            logger.info("Enviando requisição HTTP...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("Status Code: {}", response.statusCode());
            logger.info("Response Body: {}", response.body());

            if (response.statusCode() == 200) {
                // Verificar se a resposta indica sucesso
                String responseBody = response.body();
                if (responseBody.contains("\"status\":\"0\"") || responseBody.contains("\"messages\"")) {
                    logger.info("✅ SMS enviado com sucesso!");
                    return true;
                } else {
                    logger.error("❌ Erro na resposta da API: {}", responseBody);
                    return false;
                }
            } else {
                logger.error("❌ Erro HTTP. Status: {}, Response: {}", response.statusCode(), response.body());
                return false;
            }

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar SMS: {}", e.getMessage(), e);
            return false;
        }
        */
    }

    // Método alternativo para teste (sem credenciais reais)
    public boolean enviarCodigoVerificacaoTeste(String email, String celular, String codigo) {
        System.out.println("=================================================");
        System.out.println("[SMS VONAGE - MODO TESTE]");
        System.out.println("Email: " + email);
        System.out.println("Celular: " + celular);
        System.out.println("Código: " + codigo);
        System.out.println("Mensagem: 🔐 Seu código de verificação é: " + codigo);
        System.out.println("=================================================");
        return true;
    }
}

