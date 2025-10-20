package model.skill.effect;

import lombok.Getter;
import model.skill.ConditionGroup;

import java.util.List;
import java.util.Objects;

/**
 * 효과 분기 (Value Object)
 * - 조건이 참일 때 실행될 액션 목록
 * - "대신" 키워드는 stopOnMatch=true로 표현
 */
@Getter
public final class EffectBranch {
    private final ConditionGroup condition;     // null이면 무조건 실행 (기본 브랜치)
    private final List<EffectAction> actions;
    private final int order;                    // 평가 순서
    private final boolean stopOnMatch;          // "대신" 효과 (이후 브랜치 스킵)

    private EffectBranch(ConditionGroup condition, List<EffectAction> actions,
                        int order, boolean stopOnMatch) {
        this.condition = condition;
        this.actions = List.copyOf(Objects.requireNonNull(actions, "actions must not be null"));
        this.order = order;
        this.stopOnMatch = stopOnMatch;

        if (actions.isEmpty()) {
            throw new IllegalArgumentException("actions must not be empty");
        }
    }

    // Factory methods
    public static EffectBranch of(ConditionGroup condition, List<EffectAction> actions, int order) {
        return new EffectBranch(condition, actions, order, false);
    }

    public static EffectBranch defaultBranch(List<EffectAction> actions, int order) {
        return new EffectBranch(null, actions, order, false);
    }

    public static Builder builder() {
        return new Builder();
    }

    // "대신" 효과용
    public EffectBranch withStopOnMatch() {
        return new EffectBranch(this.condition, this.actions, this.order, true);
    }

    // Getter with Optional
    public java.util.Optional<ConditionGroup> getCondition() {
        return java.util.Optional.ofNullable(condition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EffectBranch that)) return false;
        return order == that.order &&
               stopOnMatch == that.stopOnMatch &&
               Objects.equals(condition, that.condition) &&
               Objects.equals(actions, that.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, actions, order, stopOnMatch);
    }

    // Builder
    public static class Builder {
        private ConditionGroup condition;
        private List<EffectAction> actions;
        private int order = 0;
        private boolean stopOnMatch = false;

        public Builder condition(ConditionGroup condition) {
            this.condition = condition;
            return this;
        }

        public Builder actions(List<EffectAction> actions) {
            this.actions = actions;
            return this;
        }

        public Builder actions(EffectAction... actions) {
            this.actions = List.of(actions);
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder stopOnMatch() {
            this.stopOnMatch = true;
            return this;
        }

        public EffectBranch build() {
            if (actions == null || actions.isEmpty()) {
                throw new IllegalStateException("actions must not be empty");
            }
            return new EffectBranch(condition, actions, order, stopOnMatch);
        }
    }
}
