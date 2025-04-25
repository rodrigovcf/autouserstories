package com.autous.autouserstories.controller;

import com.autous.autouserstories.model.EpicRequest;
import com.autous.autouserstories.model.StoryRequest;
import com.autous.autouserstories.service.Lab45Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demands")
public class Lab45Controller {

    @Autowired
    private Lab45Service lab45Service;

//    @CrossOrigin(origins = "http://localhost:3000") // Permite apenas o frontend
//    @PostMapping("/generate")
//    public ResponseEntity<String> generateLab45Request(@RequestBody StoryRequest demand) {
//        String response = lab45Service.callLab45Api(
//                demand.getUserVision(),
//                demand.getBusinessRule(),
//                demand.getAcceptanceCriteria()
//        );
//        return ResponseEntity.ok(response);
//    }

    @CrossOrigin(origins = "http://localhost:3000") // Permite apenas o frontend
    @PostMapping("/generate-user-story")
    public ResponseEntity<String> generateUserStory(@RequestBody StoryRequest demand) {
        String userStory = lab45Service.generateUserStory(demand);
        return ResponseEntity.ok(userStory);
    }

    @CrossOrigin(origins = "http://localhost:3000") // Permite apenas o frontend
    @PostMapping("/generate-epic")
    public ResponseEntity<EpicRequest> generateEpic(@RequestBody EpicRequest demand) {
        EpicRequest epic = lab45Service.generateEpic(demand);
        return ResponseEntity.ok(epic);
    }

    @CrossOrigin(origins = "http://localhost:3000") // Permite apenas o frontend
    @PostMapping("/generate-user-story-from-epic")
    public ResponseEntity<String> generateUserStoryFromEpic(@RequestBody StoryRequest demand) {
        String userStory = lab45Service.generateUserStoryFromEpic(demand);
        return ResponseEntity.ok(userStory);
    }
}