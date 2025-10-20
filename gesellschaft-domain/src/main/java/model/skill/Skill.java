package model.skill;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.Getter;
import model.AttackType;
import model.KeywordType;
import model.SinAffinity;

/**
 * 스킬 (Aggregate Root)
 * - 인격(Persona)의 개별 전투 기술
 * - 동기화 레벨별 스탯 보유
 * - 불변 객체
 */
@Getter
public final class Skill {
    private final Long id;                      // nullable (생성 시)
    private final int skillNumber;              // 1, 2, 3
    private final String name;
    private final SkillCategoryType skillCategory;
    private final SinAffinity sinAffinity;
    private final KeywordType keywordType;
    private final Integer skillQuantity;        // nullable (방어 스킬은 없음)
    private final AttackType attackType;        // nullable
    private final DefenseType defenseType;      // nullable
    private final String skillImage;            // nullable

    private final List<SkillStatsBySync> statsBySync;  // 불변 리스트

    // private 생성자
    private Skill(Long id, String name, int skillNumber,
                 SkillCategoryType skillCategory, SinAffinity sinAffinity,
                 KeywordType keywordType, Integer skillQuantity,
                 AttackType attackType, DefenseType defenseType,
                 String skillImage, List<SkillStatsBySync> statsBySync) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.skillNumber = skillNumber;
        this.skillCategory = Objects.requireNonNull(skillCategory);
        this.sinAffinity = Objects.requireNonNull(sinAffinity);
        this.keywordType = Objects.requireNonNull(keywordType);
        this.skillQuantity = skillQuantity;
        this.attackType = attackType;
        this.defenseType = defenseType;
        this.skillImage = skillImage;
        this.statsBySync = statsBySync != null ? List.copyOf(statsBySync) : List.of();
    }

    // Factory - 신규 생성
    public static Skill create(String name, int skillNumber,
                               SkillCategoryType skillCategory, SinAffinity sinAffinity,
                               KeywordType keywordType, Integer skillQuantity,
                               AttackType attackType, DefenseType defenseType,
                               String skillImage, List<SkillStatsBySync> statsBySync) {
        return new Skill(null, name, skillNumber, skillCategory, sinAffinity,
                        keywordType, skillQuantity, attackType, defenseType,
                        skillImage, statsBySync);
    }

    // ID 할당 (영속화 후)
    public Skill withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new Skill(id, this.name, this.skillNumber, this.skillCategory,
                        this.sinAffinity, this.keywordType, this.skillQuantity,
                        this.attackType, this.defenseType, this.skillImage, this.statsBySync);
    }

    // 이미지 변경 (새 객체 반환)
    public Skill changeImage(String newImage) {
        return new Skill(this.id, this.name, this.skillNumber, this.skillCategory,
                        this.sinAffinity, this.keywordType, this.skillQuantity,
                        this.attackType, this.defenseType, newImage, this.statsBySync);
    }

    // 스탯 추가 (새 객체 반환)
    public Skill addStats(SkillStatsBySync stats) {
        var updated = new java.util.ArrayList<>(this.statsBySync);
        updated.add(stats);
        return new Skill(this.id, this.name, this.skillNumber, this.skillCategory,
                        this.sinAffinity, this.keywordType, this.skillQuantity,
                        this.attackType, this.defenseType, this.skillImage, updated);
    }

    // 동기화 레벨별 스탯 조회
    public Optional<SkillStatsBySync> getStatsBySyncLevel(SyncLevel syncLevel) {
        return this.statsBySync.stream()
                .filter(stats -> stats.getSyncLevel() == syncLevel)
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill skill)) return false;
        return Objects.equals(id, skill.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Skill{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", number=" + skillNumber +
               '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private int skillNumber;
        private SkillCategoryType skillCategory;
        private SinAffinity sinAffinity;
        private KeywordType keywordType;
        private Integer skillQuantity;
        private AttackType attackType;
        private DefenseType defenseType;
        private String skillImage;
        private List<SkillStatsBySync> statsBySync;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder skillNumber(int skillNumber) {
            this.skillNumber = skillNumber;
            return this;
        }

        public Builder skillCategory(SkillCategoryType skillCategory) {
            this.skillCategory = skillCategory;
            return this;
        }

        public Builder sinAffinity(SinAffinity sinAffinity) {
            this.sinAffinity = sinAffinity;
            return this;
        }

        public Builder keywordType(KeywordType keywordType) {
            this.keywordType = keywordType;
            return this;
        }

        public Builder skillQuantity(Integer skillQuantity) {
            this.skillQuantity = skillQuantity;
            return this;
        }

        public Builder attackType(AttackType attackType) {
            this.attackType = attackType;
            return this;
        }

        public Builder defenseType(DefenseType defenseType) {
            this.defenseType = defenseType;
            return this;
        }

        public Builder skillImage(String skillImage) {
            this.skillImage = skillImage;
            return this;
        }

        public Builder statsBySync(List<SkillStatsBySync> statsBySync) {
            this.statsBySync = statsBySync;
            return this;
        }

        public Skill build() {
            return Skill.create(name, skillNumber, skillCategory, sinAffinity,
                               keywordType, skillQuantity, attackType, defenseType,
                               skillImage, statsBySync);
        }
    }
}
