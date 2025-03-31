package com.autous.autouserstories.controller;

import com.autous.autouserstories.model.DemandRequest;
import com.autous.autouserstories.service.Lab45Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demands")
public class Lab45Controller {

    @Autowired
    private Lab45Service lab45Service;

    @PostMapping("/generate")
    public ResponseEntity<String> generateUserStory(@RequestBody DemandRequest demand){
        String userStory = lab45Service.generateUserStory(demand);
        return ResponseEntity.ok(userStory);
    }
}
