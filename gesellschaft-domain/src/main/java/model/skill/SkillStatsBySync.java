package model.skill;

import lombok.Getter;
import model.skill.effect.SkillEffect;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 동기화 레벨별 스킬 스탯 (Entity)
 * - Skill의 하위 Entity
 * - 각 동기화 레벨(1~4)마다 다른 수치
 * - 스킬 효과와 코인 정보 포함
 */
@Getter
public final class SkillStatsBySync {
    private final Long id;                  // nullable (생성 시)
    private final SyncLevel syncLevel;      // 1~4
    private final int basePower;            // 기본 위력
    private final int coinPower;            // 코인 위력
    private final int coinCount;            // 코인 개수
    private final int weight;               // 가중치
    private final int level;                // 공격/방어 레벨

    // 스킬 전체 효과 (예: [사용시], [합 승리시])
    private final List<SkillEffect> skillEffects;

    // 코인별 효과
    private final List<SkillCoin> skillCoins;

    private SkillStatsBySync(Long id, SyncLevel syncLevel,
                            int basePower, int coinPower, int coinCount,
                            int weight, int level,
                            List<SkillEffect> skillEffects,
                            List<SkillCoin> skillCoins) {
        this.id = id;
        this.syncLevel = Objects.requireNonNull(syncLevel);
        this.basePower = basePower;
        this.coinPower = coinPower;
        this.coinCount = coinCount;
        this.weight = weight;
        this.level = level;
        this.skillEffects = skillEffects != null ? List.copyOf(skillEffects) : List.of();
        this.skillCoins = skillCoins != null ? List.copyOf(skillCoins) : List.of();
    }

    // Factory
    public static SkillStatsBySync create(SyncLevel syncLevel,
                                         int basePower, int coinPower, int coinCount,
                                         int weight, int level,
                                         List<SkillEffect> skillEffects,
                                         List<SkillCoin> skillCoins) {
        return new SkillStatsBySync(null, syncLevel, basePower, coinPower, coinCount,
                                   weight, level, skillEffects, skillCoins);
    }

    // ID 할당
    public SkillStatsBySync withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new SkillStatsBySync(id, this.syncLevel, this.basePower, this.coinPower,
                                   this.coinCount, this.weight, this.level,
                                   this.skillEffects, this.skillCoins);
    }

    // 스킬 효과 추가
    public SkillStatsBySync addSkillEffect(SkillEffect effect) {
        var updated = new java.util.ArrayList<>(this.skillEffects);
        updated.add(effect);
        return new SkillStatsBySync(this.id, this.syncLevel, this.basePower,
                                   this.coinPower, this.coinCount, this.weight, this.level,
                                   updated, this.skillCoins);
    }

    // 코인 추가
    public SkillStatsBySync addCoin(SkillCoin coin) {
        var updated = new java.util.ArrayList<>(this.skillCoins);
        updated.add(coin);
        return new SkillStatsBySync(this.id, this.syncLevel, this.basePower,
                                   this.coinPower, this.coinCount, this.weight, this.level,
                                   this.skillEffects, updated);
    }

    // 특정 인덱스의 코인 조회
    public Optional<SkillCoin> getCoinByIndex(int index) {
        if (index < 0 || index >= skillCoins.size()) {
            return Optional.empty();
        }
        return Optional.of(skillCoins.get(index));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillStatsBySync that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SkillStatsBySync{" +
               "id=" + id +
               ", syncLevel=" + syncLevel +
               ", basePower=" + basePower +
               ", coinCount=" + coinCount +
               '}';
    }

    // Builder
    public static Builder builder(SyncLevel syncLevel) {
        return new Builder(syncLevel);
    }

    public static class Builder {
        private final SyncLevel syncLevel;
        private int basePower;
        private int coinPower;
        private int coinCount;
        private int weight;
        private int level;
        private List<SkillEffect> skillEffects;
        private List<SkillCoin> skillCoins;

        private Builder(SyncLevel syncLevel) {
            this.syncLevel = syncLevel;
        }

        public Builder basePower(int basePower) {
            this.basePower = basePower;
            return this;
        }

        public Builder coinPower(int coinPower) {
            this.coinPower = coinPower;
            return this;
        }

        public Builder coinCount(int coinCount) {
            this.coinCount = coinCount;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder skillEffects(List<SkillEffect> skillEffects) {
            this.skillEffects = skillEffects;
            return this;
        }

        public Builder skillCoins(List<SkillCoin> skillCoins) {
            this.skillCoins = skillCoins;
            return this;
        }

        public SkillStatsBySync build() {
            return SkillStatsBySync.create(syncLevel, basePower, coinPower, coinCount,
                                          weight, level, skillEffects, skillCoins);
        }
    }
}
