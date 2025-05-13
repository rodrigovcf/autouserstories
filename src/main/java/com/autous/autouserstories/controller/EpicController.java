package com.autous.autouserstories.controller;

import com.autous.autouserstories.dto.request.EpicRequestDTO;
import com.autous.autouserstories.dto.response.EpicResponseDTO;
import com.autous.autouserstories.service.EpicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demands")
public class EpicController {

    @Autowired
    private EpicService epicService;

    @CrossOrigin(origins = "http://localhost:3000") // Allow frontend access
    @PostMapping("/generate-epic")
    public ResponseEntity<EpicResponseDTO> generateEpic(@RequestBody EpicRequestDTO demandDTO) {
        if (demandDTO == null || demandDTO.getEpicName() == null || demandDTO.getDescription() == null) {
            throw new IllegalArgumentException("Invalid payload or incorrect JSON structure.");
        }
        EpicResponseDTO epicResponse = epicService.generateEpic(demandDTO);
        return ResponseEntity.ok(epicResponse);
    }
}