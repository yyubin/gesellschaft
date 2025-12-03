package dto;

import model.skill.effect.SkillEffect;

public record SkillEffectResponse(
    String originalText
) {
    public static SkillEffectResponse from(SkillEffect effect) {
        if (effect == null) {
            return null;
        }
        return new SkillEffectResponse(
            effect.getOriginalText()
        );
    }
}
