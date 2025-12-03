package org.yyubin.gesellschaftboot.dto;

import dto.SkillResponse;
import dto.SkillStatsBySyncResponse;
import model.AttackType;
import model.KeywordType;
import model.SinAffinity;
import model.skill.DefenseType;
import model.skill.SkillCategoryType;

import java.util.List;

public record SkillResponseDto(
    Long id,
    String name,
    SinAffinity skillAttribute,
    SkillCategoryType skillCategory,
    Integer skillQuantity,
    AttackType attackType,
    DefenseType defenseType,
    Long personaId,
    String skillImage,
    List<SkillStatsBySyncResponseDto> statsBySync,
    KeywordType keyword
) {
    public static SkillResponseDto from(SkillResponse response) {
        if (response == null) return null;
        return new SkillResponseDto(
            response.id(),
            response.name(),
            response.skillAttribute(),
            response.skillCategory(),
            response.skillQuantity(),
            response.attackType(),
            response.defenseType(),
            response.personaId(),
            response.skillImage(),
            toStatsBySyncDtos(response.statsBySync()),
            response.keyword()
        );
    }

    private static List<SkillStatsBySyncResponseDto> toStatsBySyncDtos(List<SkillStatsBySyncResponse> responses) {
        if (responses == null) return List.of();
        return responses.stream()
            .map(SkillStatsBySyncResponseDto::from)
            .toList();
    }
}
