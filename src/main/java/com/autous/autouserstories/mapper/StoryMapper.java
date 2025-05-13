package com.autous.autouserstories.mapper;

import com.autous.autouserstories.dto.request.StoryRequestDTO;
import com.autous.autouserstories.dto.response.StoryResponseDTO;
import com.autous.autouserstories.model.StoryRequest;
import org.springframework.stereotype.Component;

@Component
public class StoryMapper {

    public StoryRequest toModel(StoryRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("StoryRequestDTO cannot be null");
        }

        StoryRequest.UserVision userVision = null;
        if (dto.getUserVision() != null) {
            userVision = new StoryRequest.UserVision(
                    dto.getUserVision().getAsA(),
                    dto.getUserVision().getIWant(),
                    dto.getUserVision().getSoThat()
            );
        }

        return new StoryRequest(
                dto.getUserStoryName(),
                dto.getUserStoryDescription(),
                userVision,
                dto.getOverview(),
                dto.getAssumptions(),
                dto.getNarrative(),
                dto.getTechnology(),
                dto.getAcceptanceCriteria(),
                dto.getBusinessRules(),
                dto.getMessages(),
                dto.getScreens(),
                dto.getNonFunctionalRequirements(),
                dto.getAttachments(),
                dto.getTechnicalSupplementation(),
                dto.getTechnicalAnalysis(),
                dto.getSizePFS()
        );
    }

    public StoryResponseDTO toResponseDTO(StoryRequest model) {
        if (model == null) {
            throw new IllegalArgumentException("StoryRequest cannot be null");
        }

        StoryResponseDTO.UserVisionDTO userVisionDTO = null;
        if (model.getUserVision() != null) {
            userVisionDTO = new StoryResponseDTO.UserVisionDTO(
                    model.getUserVision().getAsA(),
                    model.getUserVision().getIWant(),
                    model.getUserVision().getSoThat()
            );
        }

        return new StoryResponseDTO(
                model.getUserStoryName(),
                model.getUserStoryDescription(),
                userVisionDTO,
                model.getOverview(),
                model.getAssumptions(),
                model.getNarrative(),
                model.getTechnology(),
                model.getAcceptanceCriteria(),
                model.getBusinessRules(),
                model.getMessages(),
                model.getScreens(),
                model.getNonFunctionalRequirements(),
                model.getAttachments(),
                model.getTechnicalSupplementation(),
                model.getTechnicalAnalysis(),
                model.getSizePFS()
        );
    }
}