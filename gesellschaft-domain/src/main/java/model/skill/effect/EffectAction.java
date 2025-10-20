package model.skill.effect;

import lombok.Getter;
import model.skill.*;

import java.util.Objects;

/**
 * 효과의 구체적인 동작 (Value Object)
 * - 불변 객체
 * - Optional 대신 nullable 필드 사용
 * - 동적 계산식 지원
 */
@Getter
public final class EffectAction {

    // 기본 동작
    private final ActionType actionType;
    private final String statCode;          // "BLEED", "CHARGE", "HP" 등
    private final ActionScope scope;
    private final ApplyPolicy policy;

    // 수치 표현 (FLAT or FORMULA)
    private final AmountExpression amount;

    // 타이밍
    private final ActionTiming timing;

    // 선택적 필드 (nullable)
    private final Integer durationTurns;
    private final Integer capMax;
    private final Integer capPerTarget;
    private final TargetSelector targetSelector;
    private final CoinSelector coinSelector;

    private final int priority;

    // 생성자 - 빌더로만 생성 권장
    private EffectAction(ActionType actionType, String statCode,
                        ActionScope scope, ApplyPolicy policy,
                        AmountExpression amount, ActionTiming timing,
                        Integer durationTurns, Integer capMax, Integer capPerTarget,
                        TargetSelector targetSelector, CoinSelector coinSelector,
                        int priority) {
        this.actionType = Objects.requireNonNull(actionType, "actionType must not be null");
        this.statCode = Objects.requireNonNull(statCode, "statCode must not be null");
        this.scope = Objects.requireNonNull(scope, "scope must not be null");
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.timing = Objects.requireNonNull(timing, "timing must not be null");

        this.durationTurns = durationTurns;
        this.capMax = capMax;
        this.capPerTarget = capPerTarget;
        this.targetSelector = targetSelector;
        this.coinSelector = coinSelector;
        this.priority = priority;
    }

    // 빌더 팩토리
    public static Builder builder(ActionType actionType, String statCode) {
        return new Builder(actionType, statCode);
    }

    // Getter with Optional (API 일관성)
    public java.util.Optional<TargetSelector> getTargetSelector() {
        return java.util.Optional.ofNullable(targetSelector);
    }

    public java.util.Optional<CoinSelector> getCoinSelector() {
        return java.util.Optional.ofNullable(coinSelector);
    }

    // Value Object이므로 equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EffectAction that)) return false;
        return priority == that.priority &&
               actionType == that.actionType &&
               Objects.equals(statCode, that.statCode) &&
               scope == that.scope &&
               policy == that.policy &&
               Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionType, statCode, scope, policy, amount, priority);
    }

    @Override
    public String toString() {
        return "EffectAction{" +
               "type=" + actionType +
               ", stat=" + statCode +
               ", amount=" + amount +
               ", scope=" + scope +
               '}';
    }

    // ===== Nested Value Objects =====

    /**
     * 수치 표현 (고정값 또는 동적 계산식)
     */
    public record AmountExpression(
        AmountExprType type,
        Integer flatValue,      // FLAT 타입일 때
        String formula,         // FORMULA 타입일 때
        ActionUnit unit
    ) {
        public AmountExpression {
            Objects.requireNonNull(type, "type must not be null");
            Objects.requireNonNull(unit, "unit must not be null");

            if (type == AmountExprType.FLAT && flatValue == null) {
                throw new IllegalArgumentException("FLAT type requires flatValue");
            }
            if (type == AmountExprType.FORMULA && (formula == null || formula.isBlank())) {
                throw new IllegalArgumentException("FORMULA type requires formula");
            }
        }

        public static AmountExpression flat(int value, ActionUnit unit) {
            return new AmountExpression(AmountExprType.FLAT, value, null, unit);
        }

        public static AmountExpression formula(String expr, ActionUnit unit) {
            return new AmountExpression(AmountExprType.FORMULA, null, expr, unit);
        }

        // 계산식 예시:
        // "SELF.CHARGE / 2"
        // "(10 - SELF.CHARGE) * 2"
        // "DAMAGE_DEALT * 0.5"
        // "TARGET.BLEED * 5"
    }

    // ===== Builder =====

    public static class Builder {
        private final ActionType actionType;
        private final String statCode;

        private ActionScope scope = ActionScope.SKILL;
        private ApplyPolicy policy = ApplyPolicy.ADD;
        private AmountExpression amount;
        private ActionTiming timing = ActionTiming.IMMEDIATE;

        private Integer durationTurns;
        private Integer capMax;
        private Integer capPerTarget;
        private TargetSelector targetSelector;
        private CoinSelector coinSelector;
        private int priority = 0;

        private Builder(ActionType actionType, String statCode) {
            this.actionType = actionType;
            this.statCode = statCode;
        }

        public Builder scope(ActionScope scope) {
            this.scope = scope;
            return this;
        }

        public Builder policy(ApplyPolicy policy) {
            this.policy = policy;
            return this;
        }

        public Builder flatAmount(int value, ActionUnit unit) {
            this.amount = AmountExpression.flat(value, unit);
            return this;
        }

        public Builder formulaAmount(String expr, ActionUnit unit) {
            this.amount = AmountExpression.formula(expr, unit);
            return this;
        }

        public Builder timing(ActionTiming timing) {
            this.timing = timing;
            return this;
        }

        public Builder duration(int turns) {
            this.durationTurns = turns;
            return this;
        }

        public Builder cap(int max) {
            this.capMax = max;
            return this;
        }

        public Builder capPerTarget(int max) {
            this.capPerTarget = max;
            return this;
        }

        public Builder targetSelector(TargetSelector selector) {
            this.targetSelector = selector;
            return this;
        }

        public Builder coinSelector(CoinSelector selector) {
            this.coinSelector = selector;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public EffectAction build() {
            if (amount == null) {
                throw new IllegalStateException("Amount must be specified");
            }

            return new EffectAction(
                actionType, statCode, scope, policy,
                amount, timing,
                durationTurns, capMax, capPerTarget,
                targetSelector, coinSelector, priority
            );
        }
    }
}
