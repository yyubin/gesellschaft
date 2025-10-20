package model.passive;

import lombok.Getter;
import model.SinAffinity;

import java.util.Objects;

/**
 * 패시브 발동 조건 (Value Object)
 * - "오만 보유 3", "분노 공명 4" 등
 * - nullable (조건 없는 패시브도 있음)
 */
@Getter
public final class PassiveCondition {
    private final SinAffinity sinAffinity;
    private final ConditionType conditionType;
    private final int count;

    private PassiveCondition(SinAffinity sinAffinity, ConditionType conditionType, int count) {
        this.sinAffinity = Objects.requireNonNull(sinAffinity);
        this.conditionType = Objects.requireNonNull(conditionType);
        this.count = count;

        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
    }

    // Factory
    public static PassiveCondition of(SinAffinity sinAffinity, ConditionType conditionType, int count) {
        return new PassiveCondition(sinAffinity, conditionType, count);
    }

    // 편의 메서드
    public static PassiveCondition hold(SinAffinity sinAffinity, int count) {
        return new PassiveCondition(sinAffinity, ConditionType.HOLD, count);
    }

    public static PassiveCondition resonate(SinAffinity sinAffinity, int count) {
        return new PassiveCondition(sinAffinity, ConditionType.RESONATE, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassiveCondition that)) return false;
        return count == that.count &&
               sinAffinity == that.sinAffinity &&
               conditionType == that.conditionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sinAffinity, conditionType, count);
    }

    @Override
    public String toString() {
        return sinAffinity.getNameKo() + " " + conditionType.getNameKo() + " " + count;
    }
}
