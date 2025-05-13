package com.autous.autouserstories.mapper;

import com.autous.autouserstories.dto.request.EpicRequestDTO;
import com.autous.autouserstories.dto.response.EpicResponseDTO;
import com.autous.autouserstories.model.EpicRequest;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EpicMapper {

    public EpicRequest toModel(EpicRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("EpicRequestDTO cannot be null");
        }

        return new EpicRequest(
                dto.getEpicName(),
                dto.getEpicType(),
                dto.getDescription(),
                dto.getExpectedResult(),
                dto.getObjective(),
                dto.getSizePFS(),
                dto.getHypothesis() != null ? new EpicRequest.Hypothesis(
                        dto.getHypothesis().getCondition(),
                        dto.getHypothesis().getBelief(),
                        dto.getHypothesis().getOutcome()
                ) : null,
                dto.getIndicator(),
                dto.getTechnicalValidation(),
                dto.getRisks(),
                dto.getStoryCount(),
                dto.getFunctionalities() != null ? dto.getFunctionalities().stream()
                        .map(f -> new EpicRequest.Functionality(f.getName(), f.getDescription()))
                        .collect(Collectors.toList()) : null
        );
    }

    public EpicResponseDTO toResponseDTO(EpicRequest model) {
        if (model == null) {
            throw new IllegalArgumentException("EpicRequest cannot be null");
        }

        return new EpicResponseDTO(
                model.getEpicName(),
                model.getEpicType(),
                model.getDescription(),
                model.getExpectedResult(),
                model.getObjective(),
                model.getSizePFS(),
                model.getHypothesis() != null ? new EpicResponseDTO.HypothesisDTO(
                        model.getHypothesis().getCondition(),
                        model.getHypothesis().getBelief(),
                        model.getHypothesis().getOutcome()
                ) : null,
                model.getIndicator(),
                model.getTechnicalValidation(),
                model.getRisks(),
                model.getStoryCount(),
                model.getFunctionalities() != null ? model.getFunctionalities().stream()
                        .map(f -> new EpicResponseDTO.FunctionalityDTO(f.getName(), f.getDescription()))
                        .collect(Collectors.toList()) : null
        );
    }
}