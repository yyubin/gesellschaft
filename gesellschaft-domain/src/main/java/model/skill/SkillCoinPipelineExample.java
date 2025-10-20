package model.skill;

import model.AttackType;
import model.KeywordType;
import model.SinAffinity;
import model.skill.effect.*;

import java.util.List;

/**
 * 스킬-코인 파이프라인 실제 사용 예시
 */
public class SkillCoinPipelineExample {

    /**
     * 예시 1: 기본 공격 스킬 (3코인)
     *
     * 스킬: "잔혹한 참격"
     * - [사용시] 대상의 출혈 3 당 코인 위력 +1 (최대 3)
     * - 코인 1: [적중시] 출혈 2 부여
     * - 코인 2: [적중시] 출혈 3 부여
     * - 코인 3: [앞면 적중시] 출혈 5 부여
     */
    public static Skill example1_기본공격스킬() {
        // === 스킬 효과 (전체) ===
        var skillTrigger = new SkillTrigger(TriggerCode.ON_USE, null);

        var skillEffect = SkillEffect.builder(skillTrigger)
            .originalText("[사용시] 대상의 출혈 3 당 코인 위력 +1 (최대 3)")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.COIN_POWER_UP, "COIN_POWER")
                    .formulaAmount("TARGET.BLEED / 3", ActionUnit.FLAT)
                    .cap(3)
                    .scope(ActionScope.COINS_SKILL)
                    .build())
                .build())
            .build();

        // === 코인 효과 ===

        // 코인 1: [적중시] 출혈 2
        var coin1Effect = CoinEffect.builder(
                new SkillTrigger(TriggerCode.ON_HIT, null)
            )
            .originalText("[적중시] 출혈 2 부여")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                    .flatAmount(2, ActionUnit.STACK)
                    .targetSelector(TargetSelector.allEnemies())
                    .build())
                .build())
            .build();

        var coin1 = SkillCoin.builder(0)  // 첫 번째 코인
            .coinType(CoinType.NORMAL)
            .coinEffects(coin1Effect)
            .build();

        // 코인 2: [적중시] 출혈 3
        var coin2Effect = CoinEffect.builder(
                new SkillTrigger(TriggerCode.ON_HIT, null)
            )
            .originalText("[적중시] 출혈 3 부여")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                    .flatAmount(3, ActionUnit.STACK)
                    .targetSelector(TargetSelector.allEnemies())
                    .build())
                .build())
            .build();

        var coin2 = SkillCoin.builder(1)
            .coinType(CoinType.NORMAL)
            .coinEffects(coin2Effect)
            .build();

        // 코인 3: [앞면 적중시] 출혈 5
        var coin3Effect = CoinEffect.builder(
                new SkillTrigger(TriggerCode.ON_HEAD_HIT, null)
            )
            .originalText("[앞면 적중시] 출혈 5 부여")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                    .flatAmount(5, ActionUnit.STACK)
                    .targetSelector(TargetSelector.allEnemies())
                    .build())
                .build())
            .build();

        var coin3 = SkillCoin.builder(2)
            .coinType(CoinType.NORMAL)
            .coinEffects(coin3Effect)
            .build();

        // === 동기화별 스탯 (SYNC_3 예시) ===
        var statsBySync3 = SkillStatsBySync.builder(SyncLevel.SYNC_3)
            .basePower(4)
            .coinPower(6)
            .coinCount(3)
            .weight(1)
            .level(3)
            .skillEffects(List.of(skillEffect))
            .skillCoins(List.of(coin1, coin2, coin3))
            .build();

        // === 스킬 생성 ===
        return Skill.builder()
            .name("잔혹한 참격")
            .skillNumber(1)
            .skillCategory(SkillCategoryType.ATTACK)
            .sinAffinity(SinAffinity.WRATH)
            .keywordType(KeywordType.BLEED)
            .skillQuantity(3)
            .attackType(AttackType.SLASH)
            .statsBySync(List.of(statsBySync3))
            .build();
    }

    /**
     * 예시 2: 복잡한 코인 효과 (조건부 분기)
     *
     * 스킬: "충전 방출"
     * - [사용시] 충전 10 소모
     * - 코인 1: [적중시] 충전 5 이상이면 위력 +3, 대신 충전 3 부여
     * - 코인 2: [앞면 적중시] 대상에게 진동 (자신의 충전/2) 부여
     */
    public static Skill example2_조건부코인효과() {
        var skillTrigger = new SkillTrigger(TriggerCode.ON_USE, null);

        var skillEffect = SkillEffect.builder(skillTrigger)
            .originalText("[사용시] 충전 10 소모")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.RESOURCE_CONSUME, "CHARGE")
                    .flatAmount(10, ActionUnit.FLAT)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        // 코인 1: 조건부 분기
        var coin1Condition = ConditionGroup.and(
            StatCondition.of(ConditionTarget.SELF, "CHARGE",
                ConditionOperator.GREATER_THAN_OR_EQUAL, 5)
        );

        // Branch 1: 충전 5+ → 위력 +3
        var coin1Branch1 = EffectBranch.builder()
            .condition(coin1Condition)
            .actions(EffectAction.builder(ActionType.POWER_MODIFY, "POWER")
                .flatAmount(3, ActionUnit.FLAT)
                .scope(ActionScope.THIS_COIN)
                .build())
            .order(0)
            .stopOnMatch()  // "대신" → 다음 브랜치 스킵
            .build();

        // Branch 2: 기본 → 충전 3 부여
        var coin1Branch2 = EffectBranch.defaultBranch(
            List.of(EffectAction.builder(ActionType.STATUS_INFLICT, "CHARGE")
                .flatAmount(3, ActionUnit.STACK)
                .targetSelector(TargetSelector.self())
                .build()),
            1
        );

        var coin1Effect = CoinEffect.builder(
                new SkillTrigger(TriggerCode.ON_HIT, null)
            )
            .branches(coin1Branch1, coin1Branch2)
            .build();

        var coin1 = SkillCoin.builder(0)
            .coinEffects(coin1Effect)
            .build();

        // 코인 2: 동적 계산식
        var coin2Effect = CoinEffect.builder(
                new SkillTrigger(TriggerCode.ON_HEAD_HIT, null)
            )
            .originalText("[앞면 적중시] 대상에게 진동 (자신의 충전/2) 부여")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "TREMOR")
                    .formulaAmount("SELF.CHARGE / 2", ActionUnit.STACK)
                    .targetSelector(TargetSelector.allEnemies())
                    .build())
                .build())
            .build();

        var coin2 = SkillCoin.builder(1)
            .coinType(CoinType.UNBREAKABLE)  // 불파괴 코인
            .coinEffects(coin2Effect)
            .build();

        var statsBySync4 = SkillStatsBySync.builder(SyncLevel.SYNC_4)
            .basePower(5)
            .coinPower(8)
            .coinCount(2)
            .weight(1)
            .level(4)
            .skillEffects(List.of(skillEffect))
            .skillCoins(List.of(coin1, coin2))
            .build();

        return Skill.builder()
            .name("충전 방출")
            .skillNumber(2)
            .skillCategory(SkillCategoryType.ATTACK)
            .sinAffinity(SinAffinity.ENVY)
            .keywordType(KeywordType.CHARGE)
            .skillQuantity(2)
            .attackType(AttackType.PIERCE)
            .statsBySync(List.of(statsBySync4))
            .build();
    }

    /**
     * 예시 3: 방어 스킬 (반격)
     *
     * 스킬: "반격의 자세"
     * - [합 승리시] 자신에게 힘 1 부여
     * - 코인 1: [합 승리 적중시] 출혈 4 부여
     */
    public static Skill example3_방어스킬() {
        var skillEffect = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_WIN_CLASH, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "STR")
                    .flatAmount(1, ActionUnit.STACK)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        var coinEffect = CoinEffect.builder(
                new SkillTrigger(TriggerCode.ON_WIN_CLASH_HIT, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                    .flatAmount(4, ActionUnit.STACK)
                    .targetSelector(TargetSelector.allEnemies())
                    .build())
                .build())
            .build();

        var coin = SkillCoin.builder(0)
            .coinEffects(coinEffect)
            .build();

        var statsBySync3 = SkillStatsBySync.builder(SyncLevel.SYNC_3)
            .basePower(0)
            .coinPower(10)
            .coinCount(1)
            .weight(1)
            .level(3)
            .skillEffects(List.of(skillEffect))
            .skillCoins(List.of(coin))
            .build();

        return Skill.builder()
            .name("반격의 자세")
            .skillNumber(1)
            .skillCategory(SkillCategoryType.DEFENSE)
            .sinAffinity(SinAffinity.PRIDE)
            .keywordType(KeywordType.BLEED)
            .defenseType(DefenseType.COUNTER)
            .statsBySync(List.of(statsBySync3))
            .build();
    }

    /**
     * 예시 4: 여러 동기화 레벨 (SYNC_3, SYNC_4 모두)
     */
    public static Skill example4_다중동기화() {
        // 공통 효과
        var skillEffect = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_USE, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "DAMAGE")
                    .flatAmount(1, ActionUnit.STACK)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        var coinEffect = CoinEffect.builder(
                new SkillTrigger(TriggerCode.ON_HIT, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BURN")
                    .flatAmount(2, ActionUnit.STACK)
                    .targetSelector(TargetSelector.allEnemies())
                    .build())
                .build())
            .build();

        var coin = SkillCoin.builder(0)
            .coinEffects(coinEffect)
            .build();

        // SYNC_3: 위력 낮음
        var sync3 = SkillStatsBySync.builder(SyncLevel.SYNC_3)
            .basePower(3)
            .coinPower(5)
            .coinCount(1)
            .weight(1)
            .level(2)
            .skillEffects(List.of(skillEffect))
            .skillCoins(List.of(coin))
            .build();

        // SYNC_4: 위력 높음
        var sync4 = SkillStatsBySync.builder(SyncLevel.SYNC_4)
            .basePower(4)
            .coinPower(7)
            .coinCount(1)
            .weight(1)
            .level(3)
            .skillEffects(List.of(skillEffect))
            .skillCoins(List.of(coin))
            .build();

        return Skill.builder()
            .name("화염 방출")
            .skillNumber(1)
            .skillCategory(SkillCategoryType.ATTACK)
            .sinAffinity(SinAffinity.WRATH)
            .keywordType(KeywordType.BURN)
            .skillQuantity(1)
            .attackType(AttackType.PIERCE)
            .statsBySync(List.of(sync3, sync4))
            .build();
    }
}
