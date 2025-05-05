package com.autous.autouserstories.model;

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
@JsonRootName(value = "epic")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpicRequest {
    @JsonProperty("nome_do_epico")
    private String epicName;

    @JsonProperty("tipo_do_epico")
    private String epicType;

    @JsonProperty("descricao")
    private String description;

    @JsonProperty("resultado_esperado")
    private String expectedResult;

    @JsonProperty("objetivo")
    private String objective;

    @JsonProperty("tamanho_pfs")
    private String sizePFS;

    @JsonProperty("hipotese")
    private Hypothesis hypothesis;

    @JsonProperty("indicador")
    private String indicator;

    @JsonProperty("validacao_tecnica")
    private String technicalValidation;

    @JsonProperty("riscos")
    private String risks;

    @JsonProperty("quantidade_de_historias_do_epico")
    private int storyCount;

    @JsonProperty("lista_de_historias")
    private List<Functionality> functionalities;

    @Data
    public static class Hypothesis {
        @JsonProperty("se")
        private String condition;

        @JsonProperty("acreditamos_que")
        private String belief;

        @JsonProperty("estaremos")
        private String outcome;
    }

    @Data
    public static class Functionality {
        @JsonProperty("nome")
        private String name;

        @JsonProperty("descricao")
        private String description;
    }
}