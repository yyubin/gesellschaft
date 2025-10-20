package model.skill;

import lombok.Getter;

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
