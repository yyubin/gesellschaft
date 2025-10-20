package model.skill.effect;

import lombok.Getter;
import model.skill.ConditionGroup;
import model.skill.SkillTrigger;

import java.util.List;
import java.util.Objects;

/**
 * 코인 효과 (Aggregate Root)
 * - 특정 코인에 종속된 효과
 * - SkillEffect와 구조는 동일하지만 코인 컨텍스트
 * - 트리거 예: ON_HEAD_HIT (앞면 적중시), ON_TAIL_HIT (뒷면 적중시)
 */
@Getter
public final class CoinEffect {
    private final Long id;                      // nullable
    private final String originalText;          // 원본 텍스트

    private final SkillTrigger trigger;         // ON_HEAD_HIT, ON_TAIL_HIT 등
    private final ConditionGroup rootCondition; // nullable
    private final List<EffectBranch> branches;

    private CoinEffect(Long id, String originalText,
                      SkillTrigger trigger, ConditionGroup rootCondition,
                      List<EffectBranch> branches) {
        this.id = id;
        this.originalText = originalText;
        this.trigger = Objects.requireNonNull(trigger);
        this.rootCondition = rootCondition;
        this.branches = List.copyOf(Objects.requireNonNull(branches));

        if (branches.isEmpty()) {
            throw new IllegalArgumentException("branches must not be empty");
        }
    }

    // Factory
    public static CoinEffect create(String originalText,
                                    SkillTrigger trigger,
                                    ConditionGroup rootCondition,
                                    List<EffectBranch> branches) {
        return new CoinEffect(null, originalText, trigger, rootCondition, branches);
    }

    // ID 할당
    public CoinEffect withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new CoinEffect(id, this.originalText, this.trigger,
                             this.rootCondition, this.branches);
    }

    // 브랜치 추가
    public CoinEffect addBranch(EffectBranch newBranch) {
        var updated = new java.util.ArrayList<>(this.branches);
        updated.add(newBranch);
        return new CoinEffect(this.id, this.originalText, this.trigger,
                             this.rootCondition, updated);
    }

    // Getter with Optional
    public java.util.Optional<ConditionGroup> getRootCondition() {
        return java.util.Optional.ofNullable(rootCondition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoinEffect that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CoinEffect{" +
               "id=" + id +
               ", trigger=" + trigger +
               ", branches=" + branches.size() +
               '}';
    }

    // Builder
    public static Builder builder(SkillTrigger trigger) {
        return new Builder(trigger);
    }

    public static class Builder {
        private final SkillTrigger trigger;
        private String originalText;
        private ConditionGroup rootCondition;
        private List<EffectBranch> branches;

        private Builder(SkillTrigger trigger) {
            this.trigger = trigger;
        }

        public Builder originalText(String text) {
            this.originalText = text;
            return this;
        }

        public Builder rootCondition(ConditionGroup condition) {
            this.rootCondition = condition;
            return this;
        }

        public Builder branches(List<EffectBranch> branches) {
            this.branches = branches;
            return this;
        }

        public Builder branches(EffectBranch... branches) {
            this.branches = List.of(branches);
            return this;
        }

        public CoinEffect build() {
            if (branches == null || branches.isEmpty()) {
                throw new IllegalStateException("branches must not be empty");
            }
            return new CoinEffect(null, originalText, trigger, rootCondition, branches);
        }
    }
}
