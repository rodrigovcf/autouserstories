package com.autous.autouserstories.service;

import com.autous.autouserstories.model.DemandRequest;
import com.autous.autouserstories.model.Lab45Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class Lab45Service {

    private final WebClient webClient;

    public Lab45Service() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.lab45.ai/v1.1")
                .defaultHeader("Authorization", "Bearer Seu token aqui")
                .build();
    }

    public String generateUserStory(DemandRequest demand) {
        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Por favor, gere uma User Story baseada nas seguintes informações: \n\n" +
                        "Visão do Usuário: " + demand.getUserVision() + "\n" +
                        "Regras de negócio: " + demand.getBusinessRule() + "\n" +
                        "Critério de aceitação: " + demand.getAcceptanceCriteria() + "\n\n" +
                        "Formato esperado: \n Como [tipo de usuário], quero [objetivo] para [benefício]. " +
                        "Critérios de aceitação: [lista de condições específicas]."
        );
        message.setRole("user");

        // Configurar os parâmetros de skill
        Lab45Request.SkillParameters skillParameters = new Lab45Request.SkillParameters();
        skillParameters.setModelName("gpt-4");
        skillParameters.setRetrievalChain("custom");
        skillParameters.setMaxOutputTokens(256);
        skillParameters.setTemperature(0);
        skillParameters.setTopP(1);
        skillParameters.setFrequencyPenalty(0);
        skillParameters.setPresencePenalty(0);
        skillParameters.setEmbType("openai");
        skillParameters.setTopK(5);
        skillParameters.setReturnSources(false);

        // Criar o payload completo
        Lab45Request request = new Lab45Request();
        request.setMessages(List.of(message));
        request.setSkillParameters(skillParameters);
        request.setStreamResponse(false);

        // Enviar a requisição para o Lab45
        String response = webClient.post()
                .uri("/skills/completion/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> {
                    throw new RuntimeException("Failed to send message", error);
                })
                .block();

        // Formatar a resposta para exibição legível
        return formatResponse(response);
    }

    private String formatResponse(String response) {
        // Implementar lógica de formatação aqui
        // Por exemplo, adicionar quebras de linha, remover caracteres indesejados, etc.
        return response.replaceAll("\\\\u00e9", "é")
                .replaceAll("\\\\u00e1", "á")
                .replaceAll("\\\\u00e3", "ã")
                .replaceAll("\\\\u00f5", "õ")
                .replaceAll("\\\\u00e7", "ç")
                .replaceAll("\\\\u00f3", "ó")
                .replaceAll("\\\\u00fa", "ú")
                .replaceAll("\\\\u00ed", "í")
                .replaceAll("\\\\u00f4", "ô")
                .replaceAll("\\\\u00ea", "ê")
                .replaceAll("\\\\u00e2", "â")
                .replaceAll("\\\\u00fc", "ü")
                .replaceAll("\\\\u00f1", "ñ")
                .replaceAll("\\\\u00a0", " ")
                .replaceAll("\\\\n", "\n");
    }

    public String callLab45Api(String userVision, String businessRule, String acceptanceCriteria) {

        // Construindo a mensagem no formato do Lab45
        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Visão do Usuário: " + userVision + "\n" +
                        "Regras de negócio: " + businessRule + "\n" +
                        "Critério de aceitação: " + acceptanceCriteria
        );
        message.setRole("user");

        // Configurar os parâmetros de skill
        Lab45Request.SkillParameters skillParameters = new Lab45Request.SkillParameters();
        skillParameters.setModelName("gpt-4");
        skillParameters.setRetrievalChain("custom");
        skillParameters.setMaxOutputTokens(256);
        skillParameters.setTemperature(0);
        skillParameters.setTopP(1);
        skillParameters.setFrequencyPenalty(0);
        skillParameters.setPresencePenalty(0);
        skillParameters.setEmbType("openai");
        skillParameters.setTopK(5);
        skillParameters.setReturnSources(false);

        // Criar o payload completo
        Lab45Request request = new Lab45Request();
        request.setMessages(List.of(message));
        request.setSkillParameters(skillParameters);
        request.setStreamResponse(false);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(request);
            System.out.println("Payload JSON gerado: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Enviar a requisição para o Lab45
        String response = webClient.post()
                .uri("/skills/completion/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Formatar a resposta para exibição legível
        return formatResponse(response);
    }
}
