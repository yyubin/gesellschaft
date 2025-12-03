package org.yyubin.gesellschaftboot.dto;

import dto.SkillCoinEffectResponse;
import dto.SkillEffectResponse;
import dto.SkillStatsBySyncResponse;
import model.skill.SyncLevel;

import java.util.List;

public record SkillStatsBySyncResponseDto(
    SyncLevel syncLevel,
    int basePower,
    int coinPower,
    int coinCount,
    int weight,
    int level,
    List<SkillEffectResponseDto> skillEffects,
    List<SkillCoinEffectResponseDto> skillCoinEffects
) {
    public static SkillStatsBySyncResponseDto from(SkillStatsBySyncResponse response) {
        if (response == null) return null;
        return new SkillStatsBySyncResponseDto(
            response.syncLevel(),
            response.basePower(),
            response.coinPower(),
            response.coinCount(),
            response.weight(),
            response.level(),
            toSkillEffectDtos(response.skillEffects()),
            toCoinEffectDtos(response.skillCoinEffects())
        );
    }

    private static List<SkillEffectResponseDto> toSkillEffectDtos(List<SkillEffectResponse> responses) {
        if (responses == null) return List.of();
        return responses.stream()
            .map(SkillEffectResponseDto::from)
            .toList();
    }

    private static List<SkillCoinEffectResponseDto> toCoinEffectDtos(List<SkillCoinEffectResponse> responses) {
        if (responses == null) return List.of();
        return responses.stream()
            .map(SkillCoinEffectResponseDto::from)
            .toList();
    }
}
