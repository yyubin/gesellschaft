package model.skill;

import lombok.Getter;

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
