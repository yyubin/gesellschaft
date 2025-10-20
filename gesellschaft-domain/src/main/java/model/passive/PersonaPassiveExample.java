package model.passive;

import model.SinAffinity;
import model.skill.ConditionGroup;
import model.skill.ConditionOperator;
import model.skill.ConditionTarget;
import model.skill.SkillTrigger;
import model.skill.StatCondition;
import model.skill.SyncLevel;
import model.skill.TriggerCode;
import model.skill.effect.*;

/**
 * 인격 패시브 실제 사용 예시
 */
public class PersonaPassiveExample {

    /**
     * 예시 1: 일반 패시브 (조건 없음)
     *
     * 패시브: "전투 준비"
     * - [전투 시작 시] 자신에게 힘 2 부여
     */
    public static PersonaPassive example1_기본패시브() {
        var effect = PassiveEffect.builder(
                new SkillTrigger(TriggerCode.ON_BATTLE_START, null)
            )
            .originalText("[전투 시작 시] 자신에게 힘 2 부여")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "STR")
                    .flatAmount(2, ActionUnit.STACK)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        return PersonaPassive.builder("전투 준비", PassiveKind.NORMAL)
            .effect(effect)
            .build();
    }

    /**
     * 예시 2: 조건부 일반 패시브
     *
     * 패시브: "분노의 힘"
     * - 조건: 분노 보유 3
     * - [턴 시작 시] 자신에게 힘 1 부여
     */
    public static PersonaPassive example2_조건부패시브() {
        var condition = PassiveCondition.hold(SinAffinity.WRATH, 3);

        var effect = PassiveEffect.builder(
                new SkillTrigger(TriggerCode.ON_TURN_START, null)
            )
            .originalText("[턴 시작 시] 자신에게 힘 1 부여")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "STR")
                    .flatAmount(1, ActionUnit.STACK)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        return PersonaPassive.builder("분노의 힘", PassiveKind.NORMAL)
            .condition(condition)
            .effect(effect)
            .build();
    }

    /**
     * 예시 3: 서포트 패시브 (동기화 3)
     *
     * 패시브: "출혈 공명"
     * - 조건: 오만 공명 4
     * - [아군이 적중 시] 출혈 1 추가 부여
     */
    public static PersonaPassive example3_서포트패시브() {
        var condition = PassiveCondition.resonate(SinAffinity.PRIDE, 4);

        var effect = PassiveEffect.builder(
                new SkillTrigger(TriggerCode.ON_ALLY_HIT, null)
            )
            .originalText("[아군이 적중 시] 출혈 1 추가 부여")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                    .flatAmount(1, ActionUnit.STACK)
                    .targetSelector(TargetSelector.allEnemies())
                    .build())
                .build())
            .build();

        return PersonaPassive.builder("출혈 공명", PassiveKind.SUPPORT)
            .syncLevel(SyncLevel.SYNC_3)
            .condition(condition)
            .effect(effect)
            .build();
    }

    /**
     * 예시 4: 항상 활성 패시브
     *
     * 패시브: "불굴의 의지"
     * - [전투 중] 자신의 최대 체력 +20%
     */
    public static PersonaPassive example4_항상활성() {
        var effect = PassiveEffect.builder(
                new SkillTrigger(TriggerCode.ALWAYS, null)
            )
            .originalText("[전투 중] 자신의 최대 체력 +20%")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "MAX_HP")
                    .flatAmount(20, ActionUnit.PERCENT)
                    .targetSelector(TargetSelector.self())
                    .timing(ActionTiming.IMMEDIATE)
                    .build())
                .build())
            .build();

        return PersonaPassive.builder("불굴의 의지", PassiveKind.NORMAL)
            .effect(effect)
            .build();
    }

    /**
     * 예시 5: 복잡한 조건부 패시브
     *
     * 패시브: "출혈 폭발"
     * - 조건: 색욕 보유 4
     * - [턴 종료 시] 출혈이 10 이상인 적에게 출혈 카운트 50% 만큼의 추가 피해
     */
    public static PersonaPassive example5_복잡한패시브() {
        var activationCondition = PassiveCondition.hold(SinAffinity.LUST, 4);

        // 효과 발동 조건: 대상 출혈 10+
        var effectCondition = ConditionGroup.and(
            StatCondition.of(ConditionTarget.ENEMY, "BLEED",
                ConditionOperator.GREATER_THAN_OR_EQUAL, 10)
        );

        var effect = PassiveEffect.builder(
                new SkillTrigger(TriggerCode.ON_TURN_END, null)
            )
            .originalText("[턴 종료 시] 출혈이 10 이상인 적에게 출혈 카운트 50% 만큼의 추가 피해")
            .rootCondition(effectCondition)
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.DAMAGE_MODIFY, "DAMAGE")
                    .formulaAmount("TARGET.BLEED * 0.5", ActionUnit.FLAT)
                    .targetSelector(TargetSelector.builder(ConditionTarget.ENEMY_ALL)
                        .filterBy("BLEED", ConditionOperator.GREATER_THAN_OR_EQUAL, 10)
                        .build())
                    .build())
                .build())
            .build();

        return PersonaPassive.builder("출혈 폭발", PassiveKind.NORMAL)
            .condition(activationCondition)
            .effect(effect)
            .build();
    }

    /**
     * 예시 6: 서포트 패시브 (동기화 4, 조건부 분기)
     *
     * 패시브: "충전 지원"
     * - 조건: 질투 공명 3
     * - [아군이 공격을 가할 시] 자신의 충전이 5 이상이면 충전 3 소모하여 해당 아군에게 코인 위력 +1
     *   대신, 해당 아군에게 충전 1 부여
     */
    public static PersonaPassive example6_조건부분기서포트() {
        var activationCondition = PassiveCondition.resonate(SinAffinity.ENVY, 3);

        // Branch 1: 충전 5+ → 충전 3 소모, 코인 위력 +1
        var branchCondition = ConditionGroup.and(
            StatCondition.of(ConditionTarget.SELF, "CHARGE",
                ConditionOperator.GREATER_THAN_OR_EQUAL, 5)
        );

        var consumeCharge = EffectAction.builder(ActionType.RESOURCE_CONSUME, "CHARGE")
            .flatAmount(3, ActionUnit.FLAT)
            .targetSelector(TargetSelector.self())
            .build();

        var coinPowerUp = EffectAction.builder(ActionType.COIN_POWER_UP, "COIN_POWER")
            .flatAmount(1, ActionUnit.FLAT)
            .targetSelector(TargetSelector.builder(ConditionTarget.ALLY).build())
            .scope(ActionScope.COINS_SKILL)
            .build();

        var branch1 = EffectBranch.builder()
            .condition(branchCondition)
            .actions(consumeCharge, coinPowerUp)
            .order(0)
            .stopOnMatch()
            .build();

        // Branch 2: 기본 → 충전 1 부여
        var grantCharge = EffectAction.builder(ActionType.STATUS_INFLICT, "CHARGE")
            .flatAmount(1, ActionUnit.STACK)
            .targetSelector(TargetSelector.builder(ConditionTarget.ALLY).build())
            .build();

        var branch2 = EffectBranch.defaultBranch(
            java.util.List.of(grantCharge),
            1
        );

        var effect = PassiveEffect.builder(
                new SkillTrigger(TriggerCode.ON_ALLY_ATTACK, null)
            )
            .originalText("[아군이 공격을 가할 시] 자신의 충전이 5 이상이면 충전 3 소모하여 " +
                         "해당 아군에게 코인 위력 +1, 대신 해당 아군에게 충전 1 부여")
            .branches(branch1, branch2)
            .build();

        return PersonaPassive.builder("충전 지원", PassiveKind.SUPPORT)
            .syncLevel(SyncLevel.SYNC_4)
            .condition(activationCondition)
            .effect(effect)
            .build();
    }

    /**
     * 예시 7: 상태 이상 반응 패시브
     *
     * 패시브: "화상 면역"
     * - [화상을 받을 시] 대신 자신에게 힘 1 부여
     */
    public static PersonaPassive example7_상태이상반응() {
        // 조건: 화상을 받는 상황
        var rootCondition = ConditionGroup.and(
            StatCondition.of(ConditionTarget.SELF, "INCOMING_STATUS",
                ConditionOperator.EQUAL, 1)  // "BURN" 체크는 런타임에
        );

        // Branch 1: 화상 억제
        var suppressBurn = EffectAction.builder(ActionType.STATUS_REMOVE, "BURN")
            .policy(ApplyPolicy.SUPPRESS)
            .targetSelector(TargetSelector.self())
            .build();

        var grantStr = EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "STR")
            .flatAmount(1, ActionUnit.STACK)
            .targetSelector(TargetSelector.self())
            .build();

        var effect = PassiveEffect.builder(
                new SkillTrigger(TriggerCode.ON_STATUS_RECEIVED, null)
            )
            .originalText("[화상을 받을 시] 대신 자신에게 힘 1 부여")
            .rootCondition(rootCondition)
            .branches(EffectBranch.builder()
                .actions(suppressBurn, grantStr)
                .build())
            .build();

        return PersonaPassive.builder("화상 면역", PassiveKind.NORMAL)
            .effect(effect)
            .build();
    }
}
