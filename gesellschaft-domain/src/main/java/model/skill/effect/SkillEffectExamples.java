package model.skill.effect;

import model.skill.*;

import java.util.List;

/**
 * 실제 림버스 스킬을 도메인 모델로 표현한 예시
 */
public class SkillEffectExamples {

    /**
     * 예시 1: 충전 역장 전개3
     * "[사용시] 자신의 충전 횟수가 10 이상이면,
     *  충전 횟수를 전부 소모하여 자신을 포함하여
     *  현재 체력 비율이 낮은 아군 2명에게
     *  (소모한 충전 횟수/2)만큼 충전 역장 부여 (소수점 버림)"
     */
    public static SkillEffect example1_충전역장전개() {
        // Trigger: ON_USE
        var trigger = new SkillTrigger(TriggerCode.ON_USE, null);

        // RootCondition: 충전 >= 10
        var rootCondition = ConditionGroup.and(
            StatCondition.of(ConditionTarget.SELF, "CHARGE",
                ConditionOperator.GREATER_THAN_OR_EQUAL, 10)
        );

        // TargetSelector: 자신 포함 체력 비율 낮은 아군 2명
        var targetSelector = TargetSelector.builder(ConditionTarget.SELF_ALLY)
            .orderBy(SelectorType.LOWEST, "HP_RATIO")
            .limit(2)
            .build();

        // Actions
        var action1 = EffectAction.builder(ActionType.RESOURCE_CONSUME, "CHARGE")
            .flatAmount(999, ActionUnit.FLAT)  // 전부 소모 (999 = 무제한)
            .targetSelector(TargetSelector.self())
            .build();

        var action2 = EffectAction.builder(ActionType.STATUS_INFLICT, "CHARGE_BARRIER")
            .formulaAmount("CONSUMED_CHARGE / 2", ActionUnit.STACK)  // 동적 계산
            .targetSelector(targetSelector)
            .build();

        var branch = EffectBranch.builder()
            .actions(action1, action2)
            .order(0)
            .build();

        return SkillEffect.builder(trigger)
            .originalText("[사용시] 자신의 충전 횟수가 10 이상이면...")
            .rootCondition(rootCondition)
            .branches(branch)
            .build();
    }

    /**
     * 예시 2: 복잡한 조건부 코인 위력 증가
     * "[사용시] 충전 횟수가 5 이상 10 미만이면,
     *  최대 체력의 2 x (10 - 현재 충전 횟수)%만큼 체력을 소모하여
     *  충전 횟수가 10으로 증가
     * [사용시] 충전 횟수를 10 소모하여 코인 위력 +2"
     */
    public static List<SkillEffect> example2_충전소모위력증가() {
        var trigger = new SkillTrigger(TriggerCode.ON_USE, null);

        // 첫 번째 효과: 충전 5~10 미만일 때
        var condition1 = RangeCondition.between(ConditionTarget.SELF, "CHARGE", 5, 10);

        var consumeHp = EffectAction.builder(ActionType.CONSUME_HP, "HP")
            .formulaAmount("MAX_HP * 0.02 * (10 - SELF.CHARGE)", ActionUnit.PERCENT_OF_MAX_HP)
            .targetSelector(TargetSelector.self())
            .build();

        var setCharge = EffectAction.builder(ActionType.RESOURCE_SET, "CHARGE")
            .flatAmount(10, ActionUnit.FLAT)
            .policy(ApplyPolicy.SET)
            .targetSelector(TargetSelector.self())
            .build();

        var branch1 = EffectBranch.builder()
            .condition(ConditionGroup.and(condition1))
            .actions(consumeHp, setCharge)
            .order(0)
            .build();

        var effect1 = SkillEffect.builder(trigger)
            .branches(branch1)
            .build();

        // 두 번째 효과: 충전 10 소모 후 코인 위력 +2
        var condition2 = StatCondition.of(ConditionTarget.SELF, "CHARGE",
            ConditionOperator.GREATER_THAN_OR_EQUAL, 10);

        var consumeCharge = EffectAction.builder(ActionType.RESOURCE_CONSUME, "CHARGE")
            .flatAmount(10, ActionUnit.FLAT)
            .targetSelector(TargetSelector.self())
            .build();

        var coinPowerUp = EffectAction.builder(ActionType.COIN_POWER_UP, "COIN_POWER")
            .flatAmount(2, ActionUnit.FLAT)
            .scope(ActionScope.COINS_SKILL)
            .coinSelector(CoinSelector.all())
            .build();

        var branch2 = EffectBranch.builder()
            .condition(ConditionGroup.and(condition2))
            .actions(consumeCharge, coinPowerUp)
            .order(0)
            .build();

        var effect2 = SkillEffect.builder(trigger)
            .branches(branch2)
            .build();

        return List.of(effect1, effect2);
    }

    /**
     * 예시 3: 여러 트리거와 복잡한 조건
     * "[합 승리시] 정신력 10 회복
     * [사용시] 대상의 침잠 6 당 코인 위력 +1 (최대 2)
     * [사용시] 자신의 관 1당 피해량 +7% (최대 70%)
     * [사용시] 관 1 얻음
     * [공격 종료시] 이 스킬 공격으로 대상 처치시 관 2 얻음"
     */
    public static List<SkillEffect> example3_복합효과() {
        // Effect 1: [합 승리시] 정신력 10 회복
        var effect1 = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_WIN_CLASH, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.RESOURCE_GAIN, "SANITY")
                    .flatAmount(10, ActionUnit.FLAT)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        // Effect 2: [사용시] 대상의 침잠 6 당 코인 위력 +1 (최대 2)
        var effect2 = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_USE, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.COIN_POWER_UP, "COIN_POWER")
                    .formulaAmount("TARGET.SINKING / 6", ActionUnit.FLAT)
                    .cap(2)
                    .scope(ActionScope.COINS_SKILL)
                    .build())
                .build())
            .build();

        // Effect 3: [사용시] 자신의 관 1당 피해량 +7% (최대 70%)
        var effect3 = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_USE, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.DAMAGE_MODIFY, "DAMAGE")
                    .formulaAmount("SELF.COFFIN * 7", ActionUnit.PERCENT)
                    .cap(70)
                    .build())
                .build())
            .build();

        // Effect 4: [사용시] 관 1 얻음
        var effect4 = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_USE, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.RESOURCE_GAIN, "COFFIN")
                    .flatAmount(1, ActionUnit.FLAT)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        // Effect 5: [공격 종료시] 대상 처치시 관 2 얻음
        var effect5 = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_ATTACK_END, null)
            )
            .rootCondition(ConditionGroup.and(
                StatCondition.of(ConditionTarget.ENEMY, "IS_DEAD",
                    ConditionOperator.EQUAL, 1)
            ))
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.RESOURCE_GAIN, "COFFIN")
                    .flatAmount(2, ActionUnit.FLAT)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        return List.of(effect1, effect2, effect3, effect4, effect5);
    }

    /**
     * 예시 4: 공명 기반 조건부 원호 공격
     * "[사용시] 가장 높은 공명의 공명 당 15% 확률로 조작 패널에서
     *  자신의 우측에 위치한 아군에게 이번 턴에 원호 공격을 명령함.
     * - 가장 높은 완전 공명의 합이 4 이상이면, 대상에게 피해량 증가 1 부여
     * - 오만 완전 공명의 합이 5 이상이면, 오만 위력 증가 1 추가 부여"
     */
    public static SkillEffect example4_공명원호공격() {
        var trigger = new SkillTrigger(TriggerCode.ON_USE, null);

        // Branch 1: 원호 공격 명령 (확률 15% per 공명)
        var commandAction = EffectAction.builder(ActionType.COMMAND_ATTACK, "SUPPORT_ATTACK")
            .formulaAmount("HIGHEST_RESONANCE * 0.15", ActionUnit.PERCENT)  // 확률
            .targetSelector(TargetSelector.builder(ConditionTarget.RIGHT_ALLY).build())
            .timing(ActionTiming.THIS_TURN)
            .build();

        // Branch 2: 완전 공명 4+ → 피해량 증가 1
        var condition2 = StatCondition.of(ConditionTarget.SELF, "ABSOLUTE_RESONANCE_SUM",
            ConditionOperator.GREATER_THAN_OR_EQUAL, 4);

        var damageUpAction = EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "DAMAGE_TAKEN")
            .flatAmount(1, ActionUnit.STACK)
            .targetSelector(TargetSelector.allEnemies())
            .build();

        // Branch 3: 오만 완전 공명 5+ → 오만 위력 증가 1
        var condition3 = StatCondition.of(ConditionTarget.SELF, "PRIDE_ABSOLUTE_RESONANCE",
            ConditionOperator.GREATER_THAN_OR_EQUAL, 5);

        var pridePowerUp = EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "PRIDE_POWER")
            .flatAmount(1, ActionUnit.STACK)
            .targetSelector(TargetSelector.self())
            .build();

        return SkillEffect.builder(trigger)
            .branches(
                EffectBranch.builder().actions(commandAction).order(0).build(),
                EffectBranch.builder()
                    .condition(ConditionGroup.and(condition2))
                    .actions(damageUpAction)
                    .order(1)
                    .build(),
                EffectBranch.builder()
                    .condition(ConditionGroup.and(condition3))
                    .actions(pridePowerUp)
                    .order(2)
                    .build()
            )
            .build();
    }

    /**
     * 예시 5: 극단적으로 복잡한 케이스 (혈찬 시스템)
     * "이 스킬에서 적중 시, 입힌 체력 피해량의 50%만큼 체력 회복 (최대 10 회복)
     * [사용시] 자신의 경혈 10 당, 합 위력 +1 (최대 2)
     * [사용시] 대상의 출혈 10 이상이면, 코인 위력 +1
     * [사용시] 혈찬을 최대 50 소모하여, 소모한 혈찬 10 당, 경혈 1 얻음
     * - 혈찬을 소모하지 못했으면, 출혈 10 얻음
     * [사용시] 대상의 출혈당 피해량 +5% (최대 50%)
     * [사용시] 자신의 누적 소모 혈찬 100 당 피해량 +10% (최대 50%)
     * [공격 종료시] 대상이 사망한 경우 혈찬 30 증가. 경혈 10 얻음 (턴 당 1회)"
     */
    public static List<SkillEffect> example5_혈찬시스템() {
        // Effect 1: [적중시] 체력 회복
        var effect1 = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_HIT, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.HEAL_HP, "HP")
                    .formulaAmount("DAMAGE_DEALT * 0.5", ActionUnit.PERCENT_OF_DAMAGE)
                    .cap(10)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        // Effect 2: [사용시] 경혈 10당 합 위력 +1 (최대 2)
        var effect2 = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_USE, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.CLASH_POWER_UP, "CLASH_POWER")
                    .formulaAmount("SELF.ACUPUNCTURE / 10", ActionUnit.FLAT)
                    .cap(2)
                    .build())
                .build())
            .build();

        // Effect 3: [사용시] 대상 출혈 10+ → 코인 위력 +1
        var effect3 = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_USE, null)
            )
            .rootCondition(ConditionGroup.and(
                StatCondition.of(ConditionTarget.ENEMY, "BLEED",
                    ConditionOperator.GREATER_THAN_OR_EQUAL, 10)
            ))
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.COIN_POWER_UP, "COIN_POWER")
                    .flatAmount(1, ActionUnit.FLAT)
                    .scope(ActionScope.COINS_SKILL)
                    .build())
                .build())
            .build();

        // Effect 4: [사용시] 혈찬 소모 시스템 (분기)
        var trigger4 = new SkillTrigger(TriggerCode.ON_USE, null);

        // Branch 1: 혈찬 50 소모 성공 → 경혈 획득
        var condition4a = StatCondition.of(ConditionTarget.SELF, "BLOOD_FEAST",
            ConditionOperator.GREATER_THAN_OR_EQUAL, 1);

        var consumeBloodFeast = EffectAction.builder(ActionType.RESOURCE_CONSUME, "BLOOD_FEAST")
            .flatAmount(50, ActionUnit.FLAT)
            .cap(50)
            .targetSelector(TargetSelector.self())
            .build();

        var gainAcupuncture = EffectAction.builder(ActionType.RESOURCE_GAIN, "ACUPUNCTURE")
            .formulaAmount("CONSUMED_BLOOD_FEAST / 10", ActionUnit.FLAT)
            .targetSelector(TargetSelector.self())
            .build();

        var branch4a = EffectBranch.builder()
            .condition(ConditionGroup.and(condition4a))
            .actions(consumeBloodFeast, gainAcupuncture)
            .order(0)
            .stopOnMatch()  // "대신" 키워드 → 다음 브랜치 스킵
            .build();

        // Branch 2: 혈찬 소모 실패 → 출혈 10 획득 (기본 브랜치)
        var gainBleed = EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
            .flatAmount(10, ActionUnit.STACK)
            .targetSelector(TargetSelector.self())
            .build();

        var branch4b = EffectBranch.defaultBranch(List.of(gainBleed), 1);

        var effect4 = SkillEffect.builder(trigger4)
            .branches(branch4a, branch4b)
            .build();

        // Effect 5~7 생략 (같은 패턴)

        return List.of(effect1, effect2, effect3, effect4);
    }
}
