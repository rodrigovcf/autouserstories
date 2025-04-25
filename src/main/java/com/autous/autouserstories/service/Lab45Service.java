package com.autous.autouserstories.service;

import com.autous.autouserstories.model.EpicRequest;
import com.autous.autouserstories.model.StoryRequest;
import com.autous.autouserstories.model.Lab45Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public class Lab45Service {

    private final WebClient webClient;

    public Lab45Service(Dotenv dotenv) {
        String apiToken = dotenv.get("LAB45_API_TOKEN");
        if (apiToken == null || apiToken.isEmpty()) {
            throw new IllegalArgumentException("LAB45_API_TOKEN is not defined in .env");
        }
        this.webClient = WebClient.builder()
                .baseUrl("https://api.lab45.ai/v1.1")
                .defaultHeader("Authorization", apiToken)
                .build();
    }

    public String callApiWithTimeout(Lab45Request request) {
        return webClient.post()
                .uri("/skills/completion/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10)) // Define o timeout
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // Retry 3 vezes com 2 segundos de intervalo
                .block();
    }

    public String generateUserStory(StoryRequest demand) {
        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Por favor, gere uma User Story baseada nas seguintes informações: \n\n" +
                        "Visão do Usuário: " + demand.getUserVision() + "\n" +
                        "Regras de negócio: " + demand.getBusinessRule() + "\n" +
                        "Critério de aceitação: " + demand.getAcceptanceCriteria() + "\n\n" +
                        "Formato esperado: \n Como um [tipo de usuário] \n Eu quero: [objetivo] \n Para que: [benefício]. " +
                        "\n Critérios de aceitação: [lista de condições específicas]."
        );
        message.setRole("user");

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

        Lab45Request request = new Lab45Request();
        request.setMessages(List.of(message));
        request.setSkillParameters(skillParameters);
        request.setStreamResponse(false);

        String response = webClient.post()
                .uri("/skills/completion/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> {
                    throw new RuntimeException("Failed to send message", error);
                })
                .block();

        return formatResponse(response);
    }


    public EpicRequest generateEpic(EpicRequest demand) {
        Logger logger = LoggerFactory.getLogger(Lab45Service.class);

        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Por favor, crie um épico com base nas informações fornecidas abaixo e retorne a resposta no formato JSON. Certifique-se de preencher todos os campos com dados relevantes e contextuais, substituindo quaisquer placeholders por informações reais baseadas no contexto fornecido.\n\n" +
                        "### Informações do Épico:\n" +
                        "- Nome do Épico: " + demand.getEpicName() + "\n" +
                        "- Tipo do Épico: " + demand.getEpicType() + "\n" +
                        "- Descrição: " + demand.getDescription() + "\n\n" +
                        "- Resultado Esperado: [descreva o resultado esperado com base no contexto]\n" +
                        "- Objetivo: [descreva o objetivo com base no contexto]\n" +
                        "- Tamanho (PFS): [especifique o tamanho do épico]\n" +
                        "### Hipótese:\n" +
                        "- Se: [detalhe a condição inicial com base no contexto]\n" +
                        "- Acreditamos que: [detalhe a ação ou mudança esperada]\n" +
                        "- Estaremos: [detalhe o resultado esperado].\n\n" +
                        "- Indicador: [detalhe o indicador do épico]\n" +
                        "- Validação Técnica: [detalhe a validação técnica necessária]\n" +
                        "- Riscos: [detalhe os riscos do épico]\n" +
                        "- Quantidade de Histórias do Épico: [especifique a quantidade de histórias do usuário para o épico]\n\n" +
                        "RN - Exibir como tópicos: inclua todas as funcionalidades necessárias para atender à especificação do épico, com nomes e descrições detalhadas.\n"
        );
        message.setRole("user");

        Lab45Request.SkillParameters skillParameters = new Lab45Request.SkillParameters();
        skillParameters.setModelName("gpt-4");
        skillParameters.setRetrievalChain("custom");
        skillParameters.setMaxOutputTokens(2500);
        skillParameters.setTemperature(0);
        skillParameters.setTopP(1);
        skillParameters.setFrequencyPenalty(0);
        skillParameters.setPresencePenalty(0);
        skillParameters.setEmbType("openai");
        skillParameters.setTopK(5);
        skillParameters.setReturnSources(false);

        Lab45Request request = new Lab45Request();
        request.setMessages(List.of(message));
        request.setSkillParameters(skillParameters);
        request.setStreamResponse(false);

        String response = webClient.post()
                .uri("/skills/completion/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Log the raw API response
        logger.info("Raw API Response: {}", response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String rawData = objectMapper.readTree(response).get("data").get("content").asText();

            // Log the extracted content
            logger.info("Extracted Content: {}", rawData);

            // Remove code block markers and clean the JSON
            String cleanedJson = rawData.replaceAll("```json", "").replaceAll("```", "").trim();

            // Deserialize into EpicRequest
            return objectMapper.readValue(cleanedJson, EpicRequest.class);
        } catch (Exception e) {
            logger.error("Error during JSON deserialization", e);
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }


    public String generateUserStoryFromEpic(StoryRequest demand) {
        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Por favor, crie uma User Story detalhada, com base, no nome do epico: " + demand.getUserVision() +
                        "; e sua descrição" + demand.getBusinessRule() +" .Certifique-se de seguir a estrutura abaixo e " +
                        "de organizar o texto de forma clara e coerente para uma leitura agradável:\n\n" +

                        "### Estrutura da User Story:\n" +
                        "1. **Visão do Usuário**:\n" +
                        "   - Como [Visao do usuario com base na descrição do épico.],\n" +
                        "     Eu quero: [descreva o objetivo principal do usuário com base na descrição do épico],\n" +
                        "     Para que: [explique o benefício ou resultado esperado com base na descrição do épico].\n\n" +
                        "2. **Visão Geral**:\n" +
                        "   - [Forneça uma visão geral do contexto do épico.]\n\n" +
                        "3. **Premissas**:\n" +
                        "   - [Liste as premissas relevantes para o épico.]\n\n" +
                        "4. **Narrativa**:\n" +
                        "   - [Descreva a narrativa principal da User Story.]\n\n" +
                        "5. **Tecnologia**:\n" +
                        "   - [Especifique as tecnologias envolvidas.]\n\n" +
                        "6. **Cenários de Aceitação**:\n" +
                        "   - [Liste os cenários de aceitação detalhados.]\n\n" +
                        "7. **Regra de Negócio**:\n" +
                        "   - [Inclua as regras de negócio associadas.]\n\n" +
                        "8. **Mensagens**:\n" +
                        "   - [Detalhe as mensagens relevantes.]\n\n" +
                        "9. **Telas**:\n" +
                        "   - [Descreva as telas envolvidas.]\n\n" +
                        "10. **Requisitos Não Funcionais**:\n" +
                        "    - [Liste os requisitos não funcionais.]\n\n" +
                        "11. **Anexos**:\n" +
                        "    - [Inclua quaisquer anexos relevantes.]\n\n" +
                        "12. **Suplementação Técnica (opcional)**:\n" +
                        "    - [Adicione informações técnicas suplementares, se necessário.]\n\n" +
                        "13. **ANÁLISE TÉCNICA (grooming técnico)**:\n" +
                        "    - [Forneça uma análise técnica detalhada.]\n\n" +
                        "14. **Tamanho (PFS)**:\n" +
                        "    - [Informe o tamanho estimado em PFS.]\n\n" +
                        "Certifique-se de que todas as informações sejam coerentes e bem alinhadas com o contexto do épico fornecido." +
                        "\n\n" + "Agora como um último tópico, sugira um prompt a ser utilizado no gitubCopilot para"
                        + " dar início a demanda da história de usuário, com base no épico fornecido. O prompt deve ser claro e conciso, facilitando a compreensão do que é esperado na história de usuário." +
                          " **Prompt para o GitHub Copilot**:\n"
        );
        message.setRole("user");

        Lab45Request.SkillParameters skillParameters = new Lab45Request.SkillParameters();
        skillParameters.setModelName("gpt-4");
        skillParameters.setRetrievalChain("custom");
        skillParameters.setMaxOutputTokens(2000); // Aumentado para suportar a resposta detalhada
        skillParameters.setTemperature(0); // Ajustado para maior criatividade
        skillParameters.setTopP(1);
        skillParameters.setFrequencyPenalty(0);
        skillParameters.setPresencePenalty(0);
        skillParameters.setEmbType("openai");
        skillParameters.setTopK(5);
        skillParameters.setReturnSources(false);

        Lab45Request request = new Lab45Request();
        request.setMessages(List.of(message));
        request.setSkillParameters(skillParameters);
        request.setStreamResponse(false);

        String response = webClient.post()
                .uri("/skills/completion/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return formatResponse(response);
    }

    private String formatResponse(String response) {
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
                .replaceAll("\\\\u00c9", "É")
                .replaceAll("\\\\u00e0", "à")
                .replaceAll("\\\\u00c0", "À")
                .replaceAll("\\\\u00d3", "Ó")
                .replaceAll("\\\\u00c2", "Â")
                .replaceAll("\\\\u00ca", "Ê")
                .replaceAll("\\\\u00d4", "Ô")
                .replaceAll("\\\\u00a0", " ")
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\\"", "\"");
    }
}