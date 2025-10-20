package model.skill;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 복합 조건: 여러 조건을 AND/OR로 조합
 * - 불변 객체 (Value Object)
 * - 트리 구조
 */
@Getter
public class ConditionGroup extends AbstractCondition {
    private final LogicalOperator operator;
    private final List<AbstractCondition> children;
    private final boolean ordered;  // 순차 평가 여부 (최적화용)

    public ConditionGroup(LogicalOperator operator, List<AbstractCondition> children, boolean ordered) {
        super(ConditionScope.SKILL);  // null → SKILL
        this.operator = operator;
        this.children = List.copyOf(children);  // 불변 복사
        this.ordered = ordered;
    }

    // Factory methods
    public static ConditionGroup and(AbstractCondition... conditions) {
        return new ConditionGroup(LogicalOperator.AND, Arrays.asList(conditions), false);
    }

    public static ConditionGroup or(AbstractCondition... conditions) {
        return new ConditionGroup(LogicalOperator.OR, Arrays.asList(conditions), false);
    }

    // 평가 로직 (나중에 BattleContext 구현 시 활성화)
//    @Override
//    public boolean evaluate(BattleContext ctx) {
//        if (!ordered) {
//            return switch (operator) {
//                case AND -> children.stream().allMatch(c -> c.evaluate(ctx));
//                case OR  -> children.stream().anyMatch(c -> c.evaluate(ctx));
//            };
//        }
//
//        // 순차 평가 (short-circuit)
//        for (AbstractCondition c : children) {
//            boolean result = c.evaluate(ctx);
//            if (operator == LogicalOperator.AND && !result) return false;
//            if (operator == LogicalOperator.OR && result) return true;
//        }
//        return operator == LogicalOperator.AND;
//    }
}
