package model.skill;

import lombok.Getter;
import model.skill.effect.CoinEffect;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 스킬 코인 (Entity)
 * - 각 코인별 타입과 효과를 정의
 * - 코인 순서는 리스트 인덱스로 표현
 */
@Getter
public final class SkillCoin {
    private final Long id;                  // nullable
    private final int orderIndex;           // 코인 순서 (0부터 시작)
    private final CoinType coinType;        // NORMAL, UNBREAKABLE, etc.

    // 코인 효과 (예: [앞면 적중시], [뒷면 적중시])
    private final List<CoinEffect> coinEffects;

    private SkillCoin(Long id, int orderIndex, CoinType coinType,
                     List<CoinEffect> coinEffects) {
        this.id = id;
        this.orderIndex = orderIndex;
        this.coinType = coinType != null ? coinType : CoinType.NORMAL;
        this.coinEffects = coinEffects != null ? List.copyOf(coinEffects) : List.of();
    }

    // Factory
    public static SkillCoin create(int orderIndex, CoinType coinType,
                                   List<CoinEffect> coinEffects) {
        return new SkillCoin(null, orderIndex, coinType, coinEffects);
    }

    // ID 할당
    public SkillCoin withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new SkillCoin(id, this.orderIndex, this.coinType, this.coinEffects);
    }

    // 코인 효과 추가
    public SkillCoin addCoinEffect(CoinEffect effect) {
        var updated = new java.util.ArrayList<>(this.coinEffects);
        updated.add(effect);
        return new SkillCoin(this.id, this.orderIndex, this.coinType, updated);
    }

    // 특정 트리거의 효과 조회
    public List<CoinEffect> getEffectsByTrigger(String triggerCode) {
        return this.coinEffects.stream()
            .filter(effect -> effect.getTrigger().getCode().name().equals(triggerCode))
            .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillCoin that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SkillCoin{" +
               "id=" + id +
               ", order=" + orderIndex +
               ", type=" + coinType +
               ", effects=" + coinEffects.size() +
               '}';
    }

    // Builder
    public static Builder builder(int orderIndex) {
        return new Builder(orderIndex);
    }

    public static class Builder {
        private final int orderIndex;
        private CoinType coinType = CoinType.NORMAL;
        private List<CoinEffect> coinEffects;

        private Builder(int orderIndex) {
            this.orderIndex = orderIndex;
        }

        public Builder coinType(CoinType coinType) {
            this.coinType = coinType;
            return this;
        }

        public Builder coinEffects(List<CoinEffect> coinEffects) {
            this.coinEffects = coinEffects;
            return this;
        }

        public Builder coinEffects(CoinEffect... coinEffects) {
            this.coinEffects = List.of(coinEffects);
            return this;
        }

        public SkillCoin build() {
            return SkillCoin.create(orderIndex, coinType, coinEffects);
        }
    }
}
