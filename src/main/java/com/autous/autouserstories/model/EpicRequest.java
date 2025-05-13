package com.autous.autouserstories.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpicRequest {

    private String epicName;
    private String epicType;
    private String description;
    private String expectedResult;
    private String objective;
    private String sizePFS;
    private Hypothesis hypothesis;
    private String indicator;
    private String technicalValidation;
    private String risks;
    private int storyCount;
    private List<Functionality> functionalities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hypothesis {
        private String condition;
        private String belief;
        private String outcome;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Functionality {
        private String name;
        private String description;
    }
}