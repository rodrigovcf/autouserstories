package com.autous.autouserstories.service;

import com.autous.autouserstories.dto.request.EpicRequestDTO;
import com.autous.autouserstories.dto.response.EpicResponseDTO;
import com.autous.autouserstories.mapper.EpicMapper;
import com.autous.autouserstories.model.EpicRequest;
import com.autous.autouserstories.model.Lab45Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class EpicService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    private EpicMapper epicMapper;

    public EpicService(WebClient lab45WebClient, ObjectMapper objectMapper) {
        this.webClient = lab45WebClient;
        this.objectMapper = objectMapper;
    }

    public EpicResponseDTO generateEpic(EpicRequestDTO demandDTO) {
        Logger logger = LoggerFactory.getLogger(EpicService.class);

        // Map DTO to domain model
        EpicRequest demand = epicMapper.toModel(demandDTO);

        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Por favor, baseado nas seguintes informações:" +
                        " Nome do épico: " + demand.getEpicName() + ", " +
                        " Descrição do épico: " + demand.getDescription() + ", " +
                        " Tipo do épico: " + demand.getEpicType() + ", " +
                        " Crie um épico no contexto de metodologias ágeis e gestão de projetos, que retorne a resposta " +
                        " no formato JSON. " +
                        " Observação: Certifique-se de usar os seguintes nomes de campos no JSON:\n\n" +
                        "- nome_do_epico: " + demand.getEpicName() + "\n" +
                        "- tipo_do_epico: " + demand.getEpicType() + "\n" +
                        "- descricao: [Utilize o valor fornecido no parâmetro 'Descrição do épico'. Caso julgue necessário, " +
                        "  melhore a descrição para torná-la mais clara e coerente, estruturando-a com a composição de três " +
                        "  tópicos, onde, esses tópicos estejam separados por uma linha, mas todos como uma única string:" +
                        "   - Descrição:\n" +
                        "   - Motivação:\n" +
                        "   - Impacto Esperado:\n]\n" +
                        "- resultado_esperado: [descreva o resultado esperado com base no contexto]\n" +
                        "- objetivo: [descreva o objetivo com base no contexto]\n" +
                        "- tamanho_pfs: [especifique o tamanho do épico]\n" +
                        "### hipotese:\n" +
                        "- se: [detalhe a condição inicial com base no contexto]\n" +
                        "- acreditamos_que: [detalhe a ação ou mudança esperada]\n" +
                        "- estaremos: [detalhe o resultado esperado].\n\n" +
                        "- indicador: [detalhe o indicador do épico]\n" +
                        "- validacao_tecnica: [detalhe a validação técnica necessária]\n" +
                        "- riscos: [detalhe, textualmente, os riscos do épico]\n" +
                        "- quantidade_de_historias_do_epico: [especifique a quantidade de histórias do usuário para o épico]\n\n" +
                        "- lista_de_historias: [inclua todas as funcionalidades necessárias para atender à especificação do épico, com nomes e descrições detalhadas.]\n"
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

            // Deserialize JSON to EpicRequestDTO
            EpicRequestDTO epicResponseDTO = objectMapper.readValue(cleanedJson, EpicRequestDTO.class);

            // Map DTO to response DTO
            return epicMapper.toResponseDTO(epicMapper.toModel(epicResponseDTO));
        } catch (Exception e) {
            logger.error("Error during JSON deserialization", e);
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}