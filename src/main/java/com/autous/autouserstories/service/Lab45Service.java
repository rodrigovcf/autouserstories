package com.autous.autouserstories.service;

import com.autous.autouserstories.model.DemandRequest;
import org.springframework.stereotype.Service;

@Service
public class Lab45Service {

    public String generateUserStory(DemandRequest demand){
        //Simulando processamento do lab45
        String userStory = String.format(
            "Como um usuário, quero %s, para que eu possa %s. Critérios de aceitação: %s.",
                demand.getUserVision(),
                demand.getBusinessRule(),
                demand.getAcceptanceCriteria()
        );
        return userStory;
    }
}
