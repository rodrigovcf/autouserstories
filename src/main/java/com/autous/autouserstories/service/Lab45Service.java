package com.autous.autouserstories.service;

import com.autous.autouserstories.model.Lab45Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class Lab45Service {

    private final WebClient webClient;

    public Lab45Service(){
        this.webClient = WebClient.builder()
                .baseUrl("https://api.lab45.ai/v1.1")
                .defaultHeader("Authorization", "Bearer Bearer token|88596d02-c914-4da4-9fdc-e4f6d15b7b4d|5ef1a7cc6f1ec95fe200f81d23c9e3e1314b28f18a0bb6a4501a4923f9a500dd")
                .build();
    }

    public String callLab45Api(String userVision, String businessRule, String acceptanceCriteria){

        //Contruindo a mensagem no formato do lab45
        Lab45Request.Message message = new Lab45Request.Message();
        message.setContent(
                "Visão do Usuário: " + userVision + "\n" +
                "Regras de negócio: " +businessRule + "\n" +
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

        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String json = objectMapper.writeValueAsString(request);
            System.out.println("Payload JSON gerado: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Enviar a requisição para o Lab45
        return webClient.post()
                .uri("/skills/completion/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
