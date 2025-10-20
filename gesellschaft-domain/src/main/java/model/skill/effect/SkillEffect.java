package model.skill.effect;

import lombok.Getter;
import model.skill.ConditionGroup;
import model.skill.SkillTrigger;

import java.util.List;
import java.util.Objects;

/**
 * 스킬 효과 (Aggregate Root)
 * - 트리거 → 조건 → 분기 → 액션 구조
 * - 불변 객체
 * - 양방향 참조 제거 (단방향만)
 */
@Getter
public final class SkillEffect {
    private final Long id;                      // nullable (생성 시)
    private final String originalText;          // 원본 텍스트 (선택)

    // 트리거
    private final SkillTrigger trigger;

    // 루트 조건 (모든 브랜치 실행 전 검사)
    private final ConditionGroup rootCondition; // nullable (조건 없을 수도)

    // 효과 분기
    private final List<EffectBranch> branches;

    private SkillEffect(Long id, String originalText,
                       SkillTrigger trigger, ConditionGroup rootCondition,
                       List<EffectBranch> branches) {
        this.id = id;
        this.originalText = originalText;
        this.trigger = Objects.requireNonNull(trigger, "trigger must not be null");
        this.rootCondition = rootCondition;
        this.branches = List.copyOf(Objects.requireNonNull(branches, "branches must not be null"));

        if (branches.isEmpty()) {
            throw new IllegalArgumentException("branches must not be empty");
        }
    }

    // Factory - 신규 생성 (ID 없음)
    public static SkillEffect create(String originalText,
                                     SkillTrigger trigger,
                                     ConditionGroup rootCondition,
                                     List<EffectBranch> branches) {
        return new SkillEffect(null, originalText, trigger, rootCondition, branches);
    }

    // ID 할당 (영속화 후)
    public SkillEffect withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new SkillEffect(id, this.originalText, this.trigger, this.rootCondition, this.branches);
    }

    // 브랜치 추가 (새 객체 반환)
    public SkillEffect addBranch(EffectBranch newBranch) {
        var updated = new java.util.ArrayList<>(this.branches);
        updated.add(newBranch);
        return new SkillEffect(this.id, this.originalText, this.trigger, this.rootCondition, updated);
    }

    // 브랜치 교체
    public SkillEffect replaceBranches(List<EffectBranch> newBranches) {
        return new SkillEffect(this.id, this.originalText, this.trigger, this.rootCondition, newBranches);
    }

    // Getter with Optional
    public java.util.Optional<ConditionGroup> getRootCondition() {
        return java.util.Optional.ofNullable(rootCondition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillEffect that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SkillEffect{" +
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

        public SkillEffect build() {
            if (branches == null || branches.isEmpty()) {
                throw new IllegalStateException("branches must not be empty");
            }
            return new SkillEffect(null, originalText, trigger, rootCondition, branches);
        }
    }
}
