package com.autous.autouserstories.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryRequest {

    private String userStoryName;
    private String userStoryDescription;
    private UserVision userVision;
    private String overview;
    private List<String> assumptions;
    private String narrative;
    private String technology;
    private List<String> acceptanceCriteria;
    private List<String> businessRules;
    private List<String> messages;
    private List<String> screens;
    private List<String> nonFunctionalRequirements;
    private List<String> attachments;
    private String technicalSupplementation;
    private String technicalAnalysis;
    private String sizePFS;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserVision {
        private String asA;
        private String iWant;
        private String soThat;
    }
}