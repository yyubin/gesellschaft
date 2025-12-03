package org.yyubin.gesellschaftboot.dto;

import dto.SkillEffectResponse;

public record SkillEffectResponseDto(
    String originalText
) {
    public static SkillEffectResponseDto from(SkillEffectResponse response) {
        if (response == null) return null;
        return new SkillEffectResponseDto(
            response.originalText()
        );
    }
}
