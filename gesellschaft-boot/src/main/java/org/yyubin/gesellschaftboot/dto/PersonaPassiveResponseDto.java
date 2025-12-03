package org.yyubin.gesellschaftboot.dto;

import dto.PersonaPassiveResponse;
import model.passive.PassiveKind;
import model.skill.SyncLevel;

public record PersonaPassiveResponseDto(
    Long id,
    PassiveKind kind,
    String name,
    PassiveActivationRequirementResponseDto activation,
    String originalText,
    SyncLevel syncLevel
) {
    public static PersonaPassiveResponseDto from(PersonaPassiveResponse response) {
        if (response == null) return null;
        return new PersonaPassiveResponseDto(
            response.id(),
            response.kind(),
            response.name(),
            PassiveActivationRequirementResponseDto.from(response.activation()),
            response.originalText(),
            response.syncLevel()
        );
    }
}
