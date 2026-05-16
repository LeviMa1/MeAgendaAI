package br.com.levima.agenda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Serviço de envio de código de verificação.
 *
 * Prioridade de envio:
 *  1. CallMeBot WhatsApp — gratuito; requer que o usuário tenha se registrado
 *     previamente em https://www.callmebot.com/blog/free-api-whatsapp-messages/
 *     enviando "I allow callmebot to send me messages" para +34 644 60 81 82.
 *     Configure: CALLMEBOT_APIKEY=<apikey-do-usuario> (por enquanto suportado
 *     como envio único para número fixo em modo demo).
 *
 *  2. Vonage SMS — gratuito com crédito inicial; configure VONAGE_API_KEY e
 *     VONAGE_API_SECRET como variáveis de ambiente e reinicie a aplicação.
 *
 *  3. Modo teste — imprime o código no console.
 */
@Service
public class VerificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(VerificacaoService.class);

    @Value("${vonage.api-key:}")
    private String vonageApiKey;

    @Value("${vonage.api-secret:}")
    private String vonageApiSecret;

    @Value("${vonage.from-number:MeAgenda}")
    private String vonageFromNumber;

    // CallMeBot: cada usuário tem seu próprio apikey após opt-in
    // Por ora suportamos envio via Vonage SMS + fallback console
    @Value("${callmebot.enabled:false}")
    private boolean callmebotEnabled;

    @Value("${callmebot.apikey:}")
    private String callmebotApikey;

    public boolean enviarCodigoVerificacao(String email, String celular, String codigo) {
        String mensagem = "Seu codigo de verificacao MeAgendaAI: " + codigo +
                " | Validade: 10 minutos. Nao compartilhe este codigo.";

        // 1. Tentar CallMeBot WhatsApp (se habilitado)
        if (callmebotEnabled && !callmebotApikey.isEmpty()) {
            boolean enviado = enviarViaCallMeBot(celular, mensagem, callmebotApikey);
            if (enviado) return true;
        }

        // 2. Tentar Vonage SMS
        if (!vonageApiKey.isEmpty() && !vonageApiSecret.isEmpty()) {
            boolean enviado = enviarViaVonage(celular, mensagem);
            if (enviado) return true;
        }

        // 3. Modo teste — imprime no console
        logger.warn("⚠️  Nenhuma credencial de envio configurada. Imprimindo código no console.");
        imprimirNoConsole(email, celular, codigo);
        return true; // Retorna true para não bloquear o cadastro no modo de desenvolvimento
    }

    // ─────────────────────────────────────────
    // CallMeBot WhatsApp
    // ─────────────────────────────────────────
    private boolean enviarViaCallMeBot(String celular, String mensagem, String apikey) {
        try {
            String numeroBrasil = "55" + celular.replaceAll("[^0-9]", "");
            String url = "https://api.callmebot.com/whatsapp.php?phone=" +
                    URLEncoder.encode(numeroBrasil, StandardCharsets.UTF_8) +
                    "&text=" + URLEncoder.encode(mensagem, StandardCharsets.UTF_8) +
                    "&apikey=" + URLEncoder.encode(apikey, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("CallMeBot status: {} | body: {}", response.statusCode(), response.body());

            if (response.statusCode() == 200 && !response.body().contains("error")) {
                logger.info("✅ Código enviado via CallMeBot WhatsApp para +{}", numeroBrasil);
                return true;
            }
            logger.warn("CallMeBot retornou erro: {}", response.body());
        } catch (Exception e) {
            logger.error("Erro ao enviar via CallMeBot: {}", e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────
    // Vonage SMS
    // ─────────────────────────────────────────
    private boolean enviarViaVonage(String celular, String mensagem) {
        try {
            String numeroBrasil = "55" + celular.replaceAll("[^0-9]", "");
            logger.info("Enviando SMS Vonage para +{}", numeroBrasil);

            String urlParams = "api_key=" + URLEncoder.encode(vonageApiKey, StandardCharsets.UTF_8)
                    + "&api_secret=" + URLEncoder.encode(vonageApiSecret, StandardCharsets.UTF_8)
                    + "&to=" + URLEncoder.encode(numeroBrasil, StandardCharsets.UTF_8)
                    + "&from=" + URLEncoder.encode(vonageFromNumber, StandardCharsets.UTF_8)
                    + "&text=" + URLEncoder.encode(mensagem, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://rest.nexmo.com/sms/json?" + urlParams))
                    .GET()
                    .header("User-Agent", "MeAgenda/1.0")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Vonage status: {} | body: {}", response.statusCode(), response.body());

            if (response.statusCode() == 200 && response.body().contains("\"status\":\"0\"")) {
                logger.info("✅ SMS Vonage enviado com sucesso!");
                return true;
            }
            logger.warn("Vonage retornou erro: {}", response.body());
        } catch (Exception e) {
            logger.error("Erro ao enviar via Vonage: {}", e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────
    // Modo Teste
    // ─────────────────────────────────────────
    private void imprimirNoConsole(String email, String celular, String codigo) {
        System.out.println("=================================================");
        System.out.println("[MODO TESTE - CÓDIGO DE VERIFICAÇÃO]");
        System.out.println("Email  : " + email);
        System.out.println("Celular: " + celular);
        System.out.println("Código : " + codigo);
        System.out.println("=================================================");
    }
}
