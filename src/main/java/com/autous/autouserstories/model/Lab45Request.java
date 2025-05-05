package com.autous.autouserstories.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class Lab45Request {

    @JsonProperty("messages")
    private List<Message> messages;

    @JsonProperty("skill_parameters")
    private SkillParameters skillParameters;

    @JsonProperty("stream_response")
    private boolean streamResponse;

    @Data
    public static class Message{
        private String content;
        private String role;

    }

    @Data
    public static class SkillParameters{

        @JsonProperty("model_name")
        private String modelName;
        @JsonProperty("retrieval_chain")
        private String retrievalChain;
        @JsonProperty("max_output_tokens")
        private int maxOutputTokens;
        private double temperature;
        @JsonProperty("top_p")
        private int topP;
        @JsonProperty("frequency_penalty")
        private int frequencyPenalty;
        @JsonProperty("presence_penalty")
        private int presencePenalty;
        @JsonProperty("emb_type")
        private String embType;
        @JsonProperty("top_k")
        private int topK;
        @JsonProperty("return_sources")
        private boolean returnSources;
    }
}
