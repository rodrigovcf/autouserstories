package com.autous.autouserstories.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName(value = "user_story")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoryRequestDTO {

    @JsonProperty("nome_da_user_story")
    private String userStoryName;

    @JsonProperty("descricao_da_user_story")
    private String userStoryDescription;

    @JsonProperty("visao_do_usuario")
    private UserVisionDTO userVision;

    @JsonProperty("visao_geral")
    private String overview;

    @JsonProperty("premissas")
    private List<String> assumptions;

    @JsonProperty("narrativa")
    private String narrative;

    @JsonProperty("tecnologia")
    private String technology;

    @JsonProperty("cenarios_de_aceitacao")
    private List<String> acceptanceCriteria;

    @JsonProperty("regras_de_negocio")
    private List<String> businessRules;

    @JsonProperty("mensagens")
    private List<String> messages;

    @JsonProperty("telas")
    private List<String> screens;

    @JsonProperty("requisitos_nao_funcionais")
    private List<String> nonFunctionalRequirements;

    @JsonProperty("anexos")
    private List<String> attachments;

    @JsonProperty("suplementacao_tecnica")
    private String technicalSupplementation;

    @JsonProperty("analise_tecnica")
    private String technicalAnalysis;

    @JsonProperty("tamanho_pfs")
    private String sizePFS;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserVisionDTO {
        @JsonProperty("como")
        private String asA;

        @JsonProperty("eu_quero")
        private String iWant;

        @JsonProperty("para_que")
        private String soThat;
    }
}