package model.persona;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.Getter;
import model.GradeType;
import model.passive.PersonaPassive;
import model.skill.Skill;
import model.skill.SyncLevel;

/**
 * 인격 (Aggregate Root)
 * - 시너(Prisoner)의 전투 페르소나
 * - 스킬과 패시브 보유
 * - 불변 객체
 */
@Getter
public final class Persona {

    private final Long id;                      // nullable (생성 시)
    private final String name;
    private final String nameEn;                // nullable
    private final GradeType grade;
    private final LocalDate releaseDate;        // nullable
    private final int maxLevel;

    // 인격 정보
    private final ResistanceInfo resistanceInfo;
    private final SpeedInfo speedInfo;
    private final HealthInfo healthInfo;
    private final SeasonInfo seasonInfo;

    // 스킬 (동기화 레벨별 스탯 포함)
    private final List<Skill> skills;

    // 패시브 (일반 + 서포트)
    private final List<PersonaPassive> passives;

    // 이미지
    private final List<PersonaImage> images;

    private Persona(Long id, String name, String nameEn, GradeType grade,
                   LocalDate releaseDate, int maxLevel,
                   ResistanceInfo resistanceInfo,
                   SpeedInfo speedInfo,
                   HealthInfo healthInfo,
                   SeasonInfo seasonInfo,
                   List<Skill> skills,
                   List<PersonaPassive> passives,
                   List<PersonaImage> images) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.nameEn = nameEn;
        this.grade = Objects.requireNonNull(grade, "grade must not be null");
        this.releaseDate = releaseDate;
        this.maxLevel = maxLevel > 0 ? maxLevel : 50;  // 기본값 50
        this.resistanceInfo = resistanceInfo;
        this.speedInfo = speedInfo;
        this.healthInfo = healthInfo;
        this.seasonInfo = seasonInfo;
        this.skills = skills != null ? List.copyOf(skills) : List.of();
        this.passives = passives != null ? List.copyOf(passives) : List.of();
        this.images = images != null ? List.copyOf(images) : List.of();
    }

    // Factory - 신규 생성
    public static Persona create(String name, String nameEn, GradeType grade,
                                 LocalDate releaseDate, int maxLevel,
                                 ResistanceInfo resistanceInfo,
                                 SpeedInfo speedInfo,
                                 HealthInfo healthInfo,
                                 SeasonInfo seasonInfo,
                                 List<Skill> skills,
                                 List<PersonaPassive> passives,
                                 List<PersonaImage> images) {
        return new Persona(null, name, nameEn, grade, releaseDate, maxLevel,
                          resistanceInfo, speedInfo, healthInfo, seasonInfo,
                          skills, passives, images);
    }

    // ID 할당
    public Persona withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new Persona(id, this.name, this.nameEn, this.grade,
                          this.releaseDate, this.maxLevel,
                          this.resistanceInfo, this.speedInfo, this.healthInfo,
                          this.seasonInfo, this.skills, this.passives, this.images);
    }

    // === 스킬 관련 메서드 ===

    /**
     * 특정 번호의 스킬 조회
     * @param skillNumber 1, 2, 3
     */
    public Optional<Skill> getSkillByNumber(int skillNumber) {
        return skills.stream()
            .filter(s -> s.getSkillNumber() == skillNumber)
            .findFirst();
    }

    /**
     * 스킬 추가 (새 객체 반환)
     */
    public Persona addSkill(Skill skill) {
        var updated = new java.util.ArrayList<>(this.skills);
        updated.add(skill);
        return new Persona(this.id, this.name, this.nameEn, this.grade,
                          this.releaseDate, this.maxLevel,
                          this.resistanceInfo, this.speedInfo, this.healthInfo,
                          this.seasonInfo, updated, this.passives, this.images);
    }

    // === 패시브 관련 메서드 ===

    /**
     * 특정 동기화 레벨에서 활성 패시브 목록
     */
    public List<PersonaPassive> getActivePassivesAtSyncLevel(SyncLevel syncLevel) {
        return passives.stream()
            .filter(p -> p.isActiveAtSyncLevel(syncLevel))
            .toList();
    }

    /**
     * 일반 패시브만 조회
     */
    public List<PersonaPassive> getNormalPassives() {
        return passives.stream()
            .filter(p -> p.getKind() == model.passive.PassiveKind.NORMAL)
            .toList();
    }

    /**
     * 서포트 패시브만 조회
     */
    public List<PersonaPassive> getSupportPassives() {
        return passives.stream()
            .filter(p -> p.getKind() == model.passive.PassiveKind.SUPPORT)
            .toList();
    }

    /**
     * 패시브 추가 (새 객체 반환)
     */
    public Persona addPassive(PersonaPassive passive) {
        var updated = new java.util.ArrayList<>(this.passives);
        updated.add(passive);
        return new Persona(this.id, this.name, this.nameEn, this.grade,
                          this.releaseDate, this.maxLevel,
                          this.resistanceInfo, this.speedInfo, this.healthInfo,
                          this.seasonInfo, this.skills, updated, this.images);
    }

    // === 이미지 관련 메서드 ===

    /**
     * 대표 이미지 조회
     */
    public Optional<PersonaImage> getPrimaryImage() {
        return images.stream()
            .filter(PersonaImage::isPrimary)
            .findFirst();
    }

    /**
     * 특정 타입의 이미지 목록
     */
    public List<PersonaImage> getImagesByType(ImageType type) {
        return images.stream()
            .filter(img -> img.getType() == type)
            .toList();
    }

    /**
     * 이미지 추가 (새 객체 반환)
     */
    public Persona addImage(PersonaImage image) {
        var updated = new java.util.ArrayList<>(this.images);
        updated.add(image);
        return new Persona(this.id, this.name, this.nameEn, this.grade,
                          this.releaseDate, this.maxLevel,
                          this.resistanceInfo, this.speedInfo, this.healthInfo,
                          this.seasonInfo, this.skills, this.passives, updated);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona persona)) return false;
        return Objects.equals(id, persona.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Persona{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", grade=" + grade +
               ", skills=" + skills.size() +
               ", passives=" + passives.size() +
               '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String nameEn;
        private GradeType grade;
        private LocalDate releaseDate;
        private int maxLevel = 50;
        private ResistanceInfo resistanceInfo;
        private SpeedInfo speedInfo;
        private HealthInfo healthInfo;
        private SeasonInfo seasonInfo;
        private List<Skill> skills;
        private List<PersonaPassive> passives;
        private List<PersonaImage> images;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder nameEn(String nameEn) {
            this.nameEn = nameEn;
            return this;
        }

        public Builder grade(GradeType grade) {
            this.grade = grade;
            return this;
        }

        public Builder releaseDate(LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder resistanceInfo(ResistanceInfo resistanceInfo) {
            this.resistanceInfo = resistanceInfo;
            return this;
        }

        public Builder speedInfo(SpeedInfo speedInfo) {
            this.speedInfo = speedInfo;
            return this;
        }

        public Builder healthInfo(HealthInfo healthInfo) {
            this.healthInfo = healthInfo;
            return this;
        }

        public Builder seasonInfo(SeasonInfo seasonInfo) {
            this.seasonInfo = seasonInfo;
            return this;
        }

        public Builder skills(List<Skill> skills) {
            this.skills = skills;
            return this;
        }

        public Builder passives(List<PersonaPassive> passives) {
            this.passives = passives;
            return this;
        }

        public Builder images(List<PersonaImage> images) {
            this.images = images;
            return this;
        }

        public Persona build() {
            return Persona.create(name, nameEn, grade, releaseDate, maxLevel,
                                 resistanceInfo, speedInfo, healthInfo, seasonInfo,
                                 skills, passives, images);
        }
    }
}
