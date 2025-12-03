package dto;

import model.AttackType;
import model.KeywordType;
import model.SinAffinity;
import model.skill.DefenseType;
import model.skill.Skill;
import model.skill.SkillCategoryType;
import model.skill.SkillStatsBySync;

import java.util.List;

public record SkillResponse(
    Long id,
    String name,
    SinAffinity skillAttribute,
    SkillCategoryType skillCategory,
    Integer skillQuantity,
    AttackType attackType,
    DefenseType defenseType,
    Long personaId,
    String skillImage,
    List<SkillStatsBySyncResponse> statsBySync,
    KeywordType keyword
) {
    public static SkillResponse from(Skill skill) {
        if (skill == null) {
            return null;
        }
        return new SkillResponse(
            skill.getId(),
            skill.getName(),
            skill.getSinAffinity(),
            skill.getSkillCategory(),
            skill.getSkillQuantity(),
            skill.getAttackType(),
            skill.getDefenseType(),
            null,  // personaId는 Skill 도메인 모델에 없으므로 null
            skill.getSkillImage(),
            toStatsBySyncResponses(skill.getStatsBySync()),
            skill.getKeywordType()
        );
    }

    public static SkillResponse from(Skill skill, Long personaId) {
        if (skill == null) {
            return null;
        }
        return new SkillResponse(
            skill.getId(),
            skill.getName(),
            skill.getSinAffinity(),
            skill.getSkillCategory(),
            skill.getSkillQuantity(),
            skill.getAttackType(),
            skill.getDefenseType(),
            personaId,
            skill.getSkillImage(),
            toStatsBySyncResponses(skill.getStatsBySync()),
            skill.getKeywordType()
        );
    }

    private static List<SkillStatsBySyncResponse> toStatsBySyncResponses(List<SkillStatsBySync> statsBySync) {
        if (statsBySync == null) return List.of();
        return statsBySync.stream()
            .map(SkillStatsBySyncResponse::from)
            .toList();
    }
}
