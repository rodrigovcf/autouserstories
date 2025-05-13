package com.autous.autouserstories.controller;

import com.autous.autouserstories.dto.request.StoryRequestDTO;
import com.autous.autouserstories.dto.response.StoryResponseDTO;
import com.autous.autouserstories.mapper.StoryMapper;
import com.autous.autouserstories.model.StoryRequest;
import com.autous.autouserstories.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demands")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @Autowired
    private StoryMapper storyMapper;

    @CrossOrigin(origins = "http://localhost:3000") // Allow frontend access
    @PostMapping("/generate-user-story")
    public ResponseEntity<StoryResponseDTO> generateUserStory(@RequestBody StoryRequestDTO demandDTO) {
        if (demandDTO == null || demandDTO.getUserStoryName() == null || demandDTO.getUserStoryDescription() == null) {
            throw new IllegalArgumentException("Invalid payload or incorrect JSON structure.");
        }

        // Pass the DTO directly to the service
        StoryResponseDTO responseDTO = storyService.generateUserStory(demandDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
