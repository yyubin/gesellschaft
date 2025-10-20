package model.skill;

import lombok.Getter;

import java.util.Optional;

/**
 * 조건 검사 기본 클래스
 * - 리프 조건(StatCondition) 또는 복합 조건(ConditionGroup)
 */
@Getter
public abstract class AbstractCondition {
    private final ConditionScope scope;

    protected AbstractCondition(ConditionScope scope) {
        this.scope = scope;
    }

    // 조건 평가 메서드 (서브클래스에서 구현)
    // public abstract boolean evaluate(BattleContext ctx);
}

/**
 * 리프 조건: 특정 스탯 값 검사
 * 예: "자신의 충전 >= 10", "대상의 출혈 >= 5"
 */
@Getter
public class StatCondition extends AbstractCondition {
    private final ConditionTarget target;  // SELF, ENEMY, ALLY
    private final String statCode;         // "CHARGE", "BLEED", "HP_RATIO"
    private final ConditionOperator operator;
    private final int threshold;

    public StatCondition(ConditionTarget target, String statCode,
                         ConditionOperator operator, int threshold) {
        super(ConditionScope.SKILL);
        this.target = target;
        this.statCode = statCode;
        this.operator = operator;
        this.threshold = threshold;
    }

    // 예: SELF.CHARGE >= 10
    public static StatCondition of(ConditionTarget target, String statCode,
                                   ConditionOperator op, int value) {
        return new StatCondition(target, statCode, op, value);
    }
}

/**
 * 범위 조건: 값이 특정 구간에 있는지 검사
 * 예: "충전 >= 5 AND 충전 < 10"
 */
@Getter
public class RangeCondition extends AbstractCondition {
    private final ConditionTarget target;
    private final String statCode;
    private final int minInclusive;
    private final Integer maxExclusive;  // null이면 상한 없음

    public RangeCondition(ConditionTarget target, String statCode,
                          int minInclusive, Integer maxExclusive) {
        super(ConditionScope.SKILL);
        this.target = target;
        this.statCode = statCode;
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
    }

    public static RangeCondition between(ConditionTarget target, String statCode,
                                         int min, int max) {
        return new RangeCondition(target, statCode, min, max);
    }

    public static RangeCondition atLeast(ConditionTarget target, String statCode, int min) {
        return new RangeCondition(target, statCode, min, null);
    }
}
