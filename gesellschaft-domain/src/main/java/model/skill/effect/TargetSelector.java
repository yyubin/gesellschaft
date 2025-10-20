package model.skill.effect;

import lombok.Getter;
import model.skill.ConditionOperator;
import model.skill.ConditionTarget;

import java.util.Objects;

/**
 * 대상 선택 조건 (Value Object)
 * 예: "호흡이 6이상인 적 중 속도가 가장 느린 2명"
 */
@Getter
public final class TargetSelector {

    private final ConditionTarget pool;         // 초기 대상 풀

    // 필터 조건 (nullable)
    private final String filterStatCode;
    private final ConditionOperator filterOperator;
    private final Integer filterThreshold;

    // 정렬/선택 전략
    private final SelectorType selectorType;
    private final String orderByStatCode;       // nullable

    // 제한
    private final Integer limitCount;           // nullable (무제한)
    private final boolean excludeSelf;

    private TargetSelector(ConditionTarget pool,
                          String filterStatCode, ConditionOperator filterOperator, Integer filterThreshold,
                          SelectorType selectorType, String orderByStatCode,
                          Integer limitCount, boolean excludeSelf) {
        this.pool = Objects.requireNonNull(pool);
        this.filterStatCode = filterStatCode;
        this.filterOperator = filterOperator;
        this.filterThreshold = filterThreshold;
        this.selectorType = Objects.requireNonNull(selectorType);
        this.orderByStatCode = orderByStatCode;
        this.limitCount = limitCount;
        this.excludeSelf = excludeSelf;
    }

    // Factory methods
    public static TargetSelector self() {
        return new TargetSelector(ConditionTarget.SELF,
            null, null, null,
            SelectorType.ALL, null,
            null, false);
    }

    public static TargetSelector allEnemies() {
        return new TargetSelector(ConditionTarget.ENEMY_ALL,
            null, null, null,
            SelectorType.ALL, null,
            null, false);
    }

    public static Builder builder(ConditionTarget pool) {
        return new Builder(pool);
    }

    // Getter with Optional
    public java.util.Optional<String> getFilterStatCode() {
        return java.util.Optional.ofNullable(filterStatCode);
    }

    // Value Object
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetSelector that)) return false;
        return excludeSelf == that.excludeSelf &&
               pool == that.pool &&
               Objects.equals(filterStatCode, that.filterStatCode) &&
               filterOperator == that.filterOperator &&
               Objects.equals(filterThreshold, that.filterThreshold) &&
               selectorType == that.selectorType &&
               Objects.equals(orderByStatCode, that.orderByStatCode) &&
               Objects.equals(limitCount, that.limitCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pool, filterStatCode, selectorType, limitCount);
    }

    // ===== Builder =====

    public static class Builder {
        private final ConditionTarget pool;
        private String filterStatCode;
        private ConditionOperator filterOperator;
        private Integer filterThreshold;
        private SelectorType selectorType = SelectorType.ALL;
        private String orderByStatCode;
        private Integer limitCount;
        private boolean excludeSelf = false;

        private Builder(ConditionTarget pool) {
            this.pool = pool;
        }

        /**
         * 필터 조건 추가
         * 예: filterBy("BREATH", GREATER_THAN_OR_EQUAL, 6)
         */
        public Builder filterBy(String statCode, ConditionOperator op, int threshold) {
            this.filterStatCode = statCode;
            this.filterOperator = op;
            this.filterThreshold = threshold;
            return this;
        }

        /**
         * 정렬 기준 설정
         * 예: orderBy(SLOWEST, "SPD")
         */
        public Builder orderBy(SelectorType type, String statCode) {
            this.selectorType = type;
            this.orderByStatCode = statCode;
            return this;
        }

        public Builder limit(int count) {
            this.limitCount = count;
            return this;
        }

        public Builder excludeSelf() {
            this.excludeSelf = true;
            return this;
        }

        public TargetSelector build() {
            // 검증: HIGHEST/LOWEST 사용 시 orderByStatCode 필수
            if ((selectorType == SelectorType.HIGHEST || selectorType == SelectorType.LOWEST ||
                 selectorType == SelectorType.SLOWEST || selectorType == SelectorType.FASTEST)
                && orderByStatCode == null) {
                throw new IllegalStateException("orderByStatCode required for " + selectorType);
            }

            return new TargetSelector(pool,
                filterStatCode, filterOperator, filterThreshold,
                selectorType, orderByStatCode,
                limitCount, excludeSelf);
        }
    }
}
