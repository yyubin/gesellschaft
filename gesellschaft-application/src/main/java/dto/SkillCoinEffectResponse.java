package dto;

import model.skill.effect.CoinEffect;

public record SkillCoinEffectResponse(
    String originalText
) {
    public static SkillCoinEffectResponse from(CoinEffect effect) {
        if (effect == null) {
            return null;
        }
        return new SkillCoinEffectResponse(
            effect.getOriginalText()
        );
    }
}
