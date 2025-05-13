package com.autous.autouserstories.service;

import com.autous.autouserstories.dto.request.StoryRequestDTO;
import com.autous.autouserstories.dto.response.StoryResponseDTO;
import com.autous.autouserstories.mapper.StoryMapper;
import com.autous.autouserstories.model.Lab45Request;
import com.autous.autouserstories.model.StoryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class StoryService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    private StoryMapper storyMapper;

    public StoryService(WebClient lab45WebClient, ObjectMapper objectMapper) {
        this.webClient = lab45WebClient;
        this.objectMapper = objectMapper;
    }

    public StoryResponseDTO generateUserStory(StoryRequestDTO demandDTO) {
        Logger logger = LoggerFactory.getLogger(StoryService.class);

        // Map DTO to domain model
        StoryRequest demand = storyMapper.toModel(demandDTO);

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
                        "   - [liste os cenarios de aceitacao detalhados em formato Gherkin test.]\n\n" +
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
                        "12. **suplementacao_tecnica**:\n" +
                        "    - [descreva informacoes tecnicas suplementares com base nas informações já carregadas.]\n\n" +
                        "13. **analise_tecnica** (grooming tecnico):\n" +
                        "    - [em string, forneca uma analise tecnica detalhada, com base no nome e descricao_da_user_story.]\n\n" +
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

        logger.info("Raw API Response: {}", response);

        try {
            String rawData = objectMapper.readTree(response).get("data").get("content").asText();
            logger.info("Extracted Content: {}", rawData);

            String cleanedJson = rawData.replaceAll("```json", "").replaceAll("```", "").trim();

            // Deserialize JSON to StoryRequestDTO
            StoryRequestDTO userStoryDTO = objectMapper.readValue(cleanedJson, StoryRequestDTO.class);

            // Map DTO to domain model
            StoryRequest userStory = storyMapper.toModel(userStoryDTO);

            // Map domain model to response DTO
            return storyMapper.toResponseDTO(userStory);
        } catch (Exception e) {
            logger.error("Error during JSON deserialization", e);
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}