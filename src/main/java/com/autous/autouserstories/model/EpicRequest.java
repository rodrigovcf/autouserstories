package com.autous.autouserstories.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EpicRequest {
    @JsonProperty("Nome do Épico")
    private String epicName;

    @JsonProperty("Tipo do Épico")
    private String epicType;

    @JsonProperty("Descrição")
    private String description;

    @JsonProperty("Resultado Esperado")
    private String expectedResult;

    @JsonProperty("Objetivo")
    private String objective;

    @JsonProperty("Tamanho (PFS)")
    private String sizePFS;

    @JsonProperty("Hipótese")
    private Hypothesis hypothesis;

    @JsonProperty("Indicador")
    private String indicator;

    @JsonProperty("Validação Técnica")
    private String technicalValidation;

    @JsonProperty("Riscos")
    private String risks;

    @JsonProperty("Quantidade de Histórias do Épico")
    private int storyCount;

    @JsonProperty("RN - Exibir como tópicos")
    private List<Functionality> functionalities;


    @Data
    public static class Hypothesis {
        @JsonProperty("Se")
        private String condition;

        @JsonProperty("Acreditamos que")
        private String belief;

        @JsonProperty("Estaremos")
        private String outcome;
    }

    @Data
    public static class Functionality {
        @JsonProperty("Nome")
        private String name;

        @JsonProperty("Descrição")
        private String description;
    }
}