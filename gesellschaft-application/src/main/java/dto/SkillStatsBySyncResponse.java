package dto;

import model.skill.SkillCoin;
import model.skill.SkillStatsBySync;
import model.skill.SyncLevel;
import model.skill.effect.SkillEffect;

import java.util.List;

public record SkillStatsBySyncResponse(
    SyncLevel syncLevel,
    int basePower,
    int coinPower,
    int coinCount,
    int weight,
    int level,
    List<SkillEffectResponse> skillEffects,
    List<SkillCoinEffectResponse> skillCoinEffects
) {
    public static SkillStatsBySyncResponse from(SkillStatsBySync stats) {
        if (stats == null) {
            return null;
        }
        return new SkillStatsBySyncResponse(
            stats.getSyncLevel(),
            stats.getBasePower(),
            stats.getCoinPower(),
            stats.getCoinCount(),
            stats.getWeight(),
            stats.getLevel(),
            toSkillEffectResponses(stats.getSkillEffects()),
            toCoinEffectResponses(stats.getSkillCoins())
        );
    }

    private static List<SkillEffectResponse> toSkillEffectResponses(List<SkillEffect> effects) {
        if (effects == null) return List.of();
        return effects.stream()
            .map(SkillEffectResponse::from)
            .toList();
    }

    private static List<SkillCoinEffectResponse> toCoinEffectResponses(List<SkillCoin> coins) {
        if (coins == null) return List.of();
        return coins.stream()
            .flatMap(coin -> coin.getCoinEffects().stream())
            .map(SkillCoinEffectResponse::from)
            .toList();
    }
}
