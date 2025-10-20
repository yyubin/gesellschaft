package model.passive;

import lombok.Getter;
import model.skill.ConditionGroup;
import model.skill.SkillTrigger;
import model.skill.effect.EffectBranch;

import java.util.List;
import java.util.Objects;

/**
 * 패시브 효과 (Aggregate Root)
 * - 스킬 효과와 동일한 구조 (Trigger → Condition → Branch → Action)
 * - 차이점: 트리거가 패시브 전용 (ON_BATTLE_START, ON_TURN_START 등)
 */
@Getter
public final class PassiveEffect {
    private final Long id;                      // nullable
    private final String originalText;          // 원본 텍스트

    private final SkillTrigger trigger;         // 패시브 트리거
    private final ConditionGroup rootCondition; // nullable
    private final List<EffectBranch> branches;

    private PassiveEffect(Long id, String originalText,
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
    public static PassiveEffect create(String originalText,
                                       SkillTrigger trigger,
                                       ConditionGroup rootCondition,
                                       List<EffectBranch> branches) {
        return new PassiveEffect(null, originalText, trigger, rootCondition, branches);
    }

    // ID 할당
    public PassiveEffect withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new PassiveEffect(id, this.originalText, this.trigger,
                                this.rootCondition, this.branches);
    }

    // 브랜치 추가
    public PassiveEffect addBranch(EffectBranch newBranch) {
        var updated = new java.util.ArrayList<>(this.branches);
        updated.add(newBranch);
        return new PassiveEffect(this.id, this.originalText, this.trigger,
                                this.rootCondition, updated);
    }

    // Getter with Optional
    public java.util.Optional<ConditionGroup> getRootCondition() {
        return java.util.Optional.ofNullable(rootCondition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassiveEffect that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PassiveEffect{" +
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

        public PassiveEffect build() {
            if (branches == null || branches.isEmpty()) {
                throw new IllegalStateException("branches must not be empty");
            }
            return new PassiveEffect(null, originalText, trigger, rootCondition, branches);
        }
    }
}
