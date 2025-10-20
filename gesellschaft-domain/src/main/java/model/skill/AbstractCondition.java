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

