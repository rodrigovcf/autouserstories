package com.autous.autouserstories.controller;

import com.autous.autouserstories.model.FunctionPointsRequest;
import com.autous.autouserstories.service.FunctionPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demands")
public class FunctionPointsController {

    @Autowired
    private FunctionPointsService functionPointsServiceService;

    @CrossOrigin(origins = "http://localhost:3000") // Allow frontend access
    @PostMapping("/calculate-function-points")
    public ResponseEntity<FunctionPointsRequest> calculateFunctionPoints(@RequestBody FunctionPointsRequest demand) {
        if (demand == null || demand.getStringStory() == null || demand.getFunctionPointsDefinitions() == null) {
            throw new IllegalArgumentException("Invalid payload or incorrect JSON structure.");
        }
        FunctionPointsRequest pf = functionPointsServiceService.calculateFunctionPoints(demand);
        System.out.println("Payload recebido: " + demand);
        return ResponseEntity.ok(pf);
    }

}