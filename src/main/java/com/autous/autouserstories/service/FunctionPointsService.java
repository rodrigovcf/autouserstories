package com.autous.autouserstories.service;

import com.autous.autouserstories.model.FunctionPointsRequest;
import com.autous.autouserstories.model.Lab45Request;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;

@Service
public class FunctionPointsService {


    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public FunctionPointsService(WebClient lab45WebClient, ObjectMapper objectMapper) {
        this.webClient = lab45WebClient;
        this.objectMapper = objectMapper;
    }


    public FunctionPointsRequest calculateFunctionPoints(FunctionPointsRequest demand) {
        Logger logger = LoggerFactory.getLogger(FunctionPointsService.class);

        String msgPost = String.format(
                "Por favor, me retorne o valor de PONTO DE FUNÇÃO da história abaixo, avaliando o texto com base nos critérios:\n\n" +
                        "1. **Critérios de cálculo do ponto de função simplificado**.\n" +
                        "2. **Critérios de cálculo do ponto de função de projetos cascatas**.\n\n" +
                        "Compare o \"texto da história\" com o \"texto das métricas de pontos de função\" e me retorne, por exemplo:\n\n" +
                        "\"Esta história se encaixa em Pontos_funcao_simplificado=1,5 e Pontos_funcao_projeto_cascatas=2,9\".\n\n" +
                        "**Texto da história**:\n%s\n\n" +
                        "**Texto com as métricas de PONTOS DE FUNÇÃO**:\n%s" +
                        "Observação: O retorno deve ser feito em formato JSON",
                demand.getStringStory(),
                demand.getFunctionPointsDefinitions()
        );

        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(msgPost);
        message.setRole("user");

        Lab45Request.SkillParameters skillParameters = new Lab45Request.SkillParameters();
        skillParameters.setModelName("gpt-4o");
        skillParameters.setRetrievalChain("custom");
        skillParameters.setMaxOutputTokens(2500);
        skillParameters.setTemperature(0);
        skillParameters.setTopP(1);
        skillParameters.setFrequencyPenalty(0);
        skillParameters.setPresencePenalty(0);
        skillParameters.setEmbType("openai");
        skillParameters.setTopK(5);
        skillParameters.setReturnSources(false);

        Lab45Request lab45Request = new Lab45Request();
        lab45Request.setMessages(List.of(message));
        lab45Request.setSkillParameters(skillParameters);
        lab45Request.setStreamResponse(false);

        String response = webClient.post()
                .uri("/skills/completion/query")
                .bodyValue(lab45Request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Log the raw API response
        logger.info("Raw API Response: {}", response);

        try {
            // Verifica se o conteúdo é um JSON válido
            JsonNode jsonNode = objectMapper.readTree(response);
            String rawData = jsonNode.get("data").get("content").asText();

            // Log the extracted content
            logger.info("Extracted Content: {}", rawData);

            // Remove code block markers and clean the JSON
            String cleanedJson = rawData.replaceAll("```json", "").replaceAll("```", "").trim();

            // Deserialize into FunctionPointsRequest
            return objectMapper.readValue(cleanedJson, FunctionPointsRequest.class);
        } catch (Exception e) {
            logger.error("Error during JSON deserialization", e);
            throw new RuntimeException("Failed to parse JSON response. Ensure the API returns valid JSON.", e);
        }
    }
}
