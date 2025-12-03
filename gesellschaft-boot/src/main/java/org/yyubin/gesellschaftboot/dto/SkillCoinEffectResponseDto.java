package org.yyubin.gesellschaftboot.dto;

import dto.SkillCoinEffectResponse;

public record SkillCoinEffectResponseDto(
    String originalText
) {
    public static SkillCoinEffectResponseDto from(SkillCoinEffectResponse response) {
        if (response == null) return null;
        return new SkillCoinEffectResponseDto(
            response.originalText()
        );
    }
}
