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

import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;

@Service
public class Lab45Service {


    private final WebClient webClient;
    private final ObjectMapper objectMapper; // Injetando o ObjectMapper configurado

    public Lab45Service(Dotenv dotenv, ObjectMapper objectMapper) {
        String apiToken = dotenv.get("LAB45_API_TOKEN");
        if (apiToken == null || apiToken.isEmpty()) {
            throw new IllegalArgumentException("LAB45_API_TOKEN is not defined in .env");
        }
        this.webClient = WebClient.builder()
                .baseUrl("https://api.lab45.ai/v1.1")
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .build();
        this.objectMapper = objectMapper; // Atribuindo a instância injetada
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

    public StoryRequest generateUserStory(StoryRequest demand) {
        Logger logger = LoggerFactory.getLogger(Lab45Service.class);

        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Por favor, crie uma user story com base nas informações fornecidas abaixo, focando no seu nome e descrição. " +
                        "É importante preencher os demais itens com base nas suas respectivas informações:\n\n" +
                        "- **nome_da_user_story**: " + demand.getUserStoryName() + "\n" +
                        "- **descricao_da_user_story**: " + demand.getUserStoryDescription() + "\n\n" +
                        "Certifique-se de seguir a estrutura abaixo e organizar o texto de forma clara e coerente para uma " +
                        "leitura agradável e retorne a resposta no formato JSON.:\n\n" +
                        "### Estrutura da User Story:\n" +
                        "1. **visao_do_usuario**:\n" +
                        "   - como [visao do usuario com base na descricao_da_user_story.],\n" +
                        "     eu_quero: [descreva o objetivo principal do usuario com base na descricao_da_user_story.],\n" +
                        "     para_que: [explique o beneficio ou resultado esperado com base na descricao_da_user_story.]\n\n" +
                        "2. **visao_geral**:\n" +
                        "   - [forneca uma visao geral do contexto da user story.]\n\n" +
                        "3. **premissas**:\n" +
                        "   - [liste as premissas relevantes para a user story.]\n\n" +
                        "4. **narrativa**:\n" +
                        "   - [descreva a narrativa principal da user story.]\n\n" +
                        "5. **tecnologia**:\n" +
                        "   - [em string, descreva especificamente as possiveis tecnologias envolvidas.]\n\n" +
                        "6. **cenarios_de_aceitacao**:\n" +
                        "   - [liste os cenarios de aceitacao detalhados.]\n\n" +
                        "7. **regras_de_negocio**:\n" +
                        "   - [inclua as regras de negocio associadas.]\n\n" +
                        "8. **mensagens**:\n" +
                        "   - [detalhe as mensagens relevantes.]\n\n" +
                        "9. **telas**:\n" +
                        "   - [descreva as telas envolvidas.]\n\n" +
                        "10. **requisitos_nao_funcionais**:\n" +
                        "    - [liste os requisitos nao funcionais.]\n\n" +
                        "11. **anexos**:\n" +
                        "    - inclua quaisquer anexos relevantes, em topicos.\n\n" +
                        "12. **suplementacao_tecnica** (opcional):\n" +
                        "    - [adicione informacoes tecnicas suplementares, se necessario, em formato descritivo/dissertativo.]\n\n" +
                        "13. **analise_tecnica** (grooming tecnico):\n" +
                        "    - [forneca uma analise tecnica detalhada, com base no nome e descricao_da_user_story.]\n\n" +
                        "14. **tamanho_pfs**:\n" +
                        "    - [informe o tamanho estimado em PFS.]\n\n" +
                        "**prompt_para_o_github_copilot**:\n" +
                        " [agora, nesse ultimo topico, caso a historia envolva a necessidade de implementacao de codigo," +
                        "sugira um prompt que possa ser utilizado no github_copilot para dar inicio a referida demanda " +
                        "com base, nos dados ja fornecidos e gerados. Caso o contexto nao se enquadre em demanda de implementacao," +
                        " o topico deve ser carregado com a mensagem: nao atende.]" +

                        "Obs.: certifique-se de que todas as informacoes geradas sejam coerentes e bem alinhadas com o " +
                        "contexto de cada topico da user story fornecida."
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
            String rawData = objectMapper.readTree(response).get("data").get("content").asText();

            // Log the extracted content
            logger.info("Extracted Content: {}", rawData);

            // Remove code block markers and clean the JSON
            String cleanedJson = rawData.replaceAll("```json", "").replaceAll("```", "").trim();

            // Deserialize into EpicRequest
            return objectMapper.readValue(cleanedJson, StoryRequest.class);
        } catch (Exception e) {
            logger.error("Error during JSON deserialization", e);
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }

    public EpicRequest generateEpic(EpicRequest demand) {
        Logger logger = LoggerFactory.getLogger(Lab45Service.class);

        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Por favor, crie um épico com base nas informações fornecidas abaixo e retorne a resposta no formato JSON. " +
                        "Certifique-se de usar os seguintes nomes de campos no JSON:\n\n" +
                        "- nome_do_epico: " + demand.getEpicName() + "\n" +
                        "- tipo_do_epico: " + demand.getEpicType() + "\n" +
                        "- descricao: " + demand.getDescription() + " A descricao deve ser retornada com o conteudo já" +
                        "   enviado como parâmetro, seguido de mais dois tópicos:" +
                        "   - Motivação:\n" +
                        "   - Impacto Esperado:\n\n" +
                        "- resultado_esperado: [descreva o resultado esperado com base no contexto]\n" +
                        "- objetivo: [descreva o objetivo com base no contexto]\n" +
                        "- tamanho_pfs: [especifique o tamanho do épico]\n" +
                        "### hipotese:\n" +
                        "- se: [detalhe a condição inicial com base no contexto]\n" +
                        "- acreditamos_que: [detalhe a ação ou mudança esperada]\n" +
                        "- estaremos: [detalhe o resultado esperado].\n\n" +
                        "- indicador: [detalhe o indicador do épico]\n" +
                        "- validacao_tecnica: [detalhe a validação técnica necessária]\n" +
                        "- riscos: [detalhe os riscos do épico]\n" +
                        "- quantidade_de_historias_do_epico: [especifique a quantidade de histórias do usuário para o épico]\n\n" +
                        "lista_de_historias: [inclua todas as funcionalidades necessárias para atender à especificação do épico, com nomes e descrições detalhadas.]\n"
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
                "Por favor, crie uma User Story detalhada, com base, no nome do epico: " + demand.getUserStoryName() +
                        "; e sua descrição" + demand.getUserStoryDescription() + " .Certifique-se de seguir a estrutura abaixo e " +
                        "de organizar o texto de forma clara e coerente para uma leitura agradável:\n\n" +

                        "### Estrutura da User Story:\n" +
                        "1. **Visão do Usuário**:\n" +
                        "   - Como [Visao do usuario com base na descrição do épico. Omitir o termo -como- para não ficar redundante],\n" +
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
