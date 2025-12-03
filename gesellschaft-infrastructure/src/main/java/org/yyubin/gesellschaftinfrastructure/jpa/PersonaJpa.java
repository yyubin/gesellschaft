package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.GradeType;
import model.persona.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Persona JPA 엔티티
 * - 인격 (시너의 전투 페르소나)
 * - Aggregate Root
 */
@Entity
@Table(name = "persona")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PersonaJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String nameEn;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GradeType grade;

    @Column
    private LocalDate releaseDate;

    @Column(nullable = false)
    private int maxLevel = 50;

    // === 내성 정보 (Embedded) ===
    @Enumerated(EnumType.STRING)
    @Column(name = "slash_resistance", length = 20)
    private ResistanceType slashResistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "penetration_resistance", length = 20)
    private ResistanceType penetrationResistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "blunt_resistance", length = 20)
    private ResistanceType bluntResistance;

    // === 속도 정보 (Embedded) ===
    @Column(name = "min_speed")
    private int minSpeed;

    @Column(name = "max_speed")
    private int maxSpeed;

    // === 체력 정보 (Embedded) ===
    @Column(name = "base_health")
    private int baseHealth;

    @Column(name = "growth_rate")
    private double growthRate;

    @Column(name = "disturbed1")
    private int disturbed1;

    @Column(name = "disturbed2")
    private int disturbed2;

    @Column(name = "disturbed3")
    private int disturbed3;

    // === 시즌 정보 (Embedded) ===
    @Enumerated(EnumType.STRING)
    @Column(name = "season_type", length = 30)
    private SeasonType seasonType;

    @Column(name = "season_number")
    private Integer seasonNumber;

    // === 추가 정보 ===
    @Column(name = "defense_level")
    private int defenseLevel;

    private int mentality;

    // === 관계 ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinner_id")
    private SinnerJpa sinner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id")
    private SubAffiliationJpa affiliation;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillJpa> skills = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaPassiveJpa> passives = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaImageJpa> images = new ArrayList<>();

    @Builder
    public PersonaJpa(String name, String nameEn, GradeType grade, LocalDate releaseDate, int maxLevel,
                      ResistanceType slashResistance, ResistanceType penetrationResistance,
                      ResistanceType bluntResistance, int minSpeed, int maxSpeed,
                      int baseHealth, double growthRate, int disturbed1, int disturbed2, int disturbed3,
                      SeasonType seasonType, Integer seasonNumber,
                      int defenseLevel, int mentality) {
        this.name = name;
        this.nameEn = nameEn;
        this.grade = grade;
        this.releaseDate = releaseDate;
        this.maxLevel = maxLevel;
        this.slashResistance = slashResistance;
        this.penetrationResistance = penetrationResistance;
        this.bluntResistance = bluntResistance;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.baseHealth = baseHealth;
        this.growthRate = growthRate;
        this.disturbed1 = disturbed1;
        this.disturbed2 = disturbed2;
        this.disturbed3 = disturbed3;
        this.seasonType = seasonType;
        this.seasonNumber = seasonNumber;
        this.defenseLevel = defenseLevel;
        this.mentality = mentality;
    }

    // ID를 포함한 전체 생성자 (매퍼 전용)
    public PersonaJpa(Long id, String name, String nameEn, GradeType grade, LocalDate releaseDate, int maxLevel,
                      ResistanceType slashResistance, ResistanceType penetrationResistance,
                      ResistanceType bluntResistance, int minSpeed, int maxSpeed,
                      int baseHealth, double growthRate, int disturbed1, int disturbed2, int disturbed3,
                      SeasonType seasonType, Integer seasonNumber,
                      int defenseLevel, int mentality, SubAffiliationJpa affiliation) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.grade = grade;
        this.releaseDate = releaseDate;
        this.maxLevel = maxLevel;
        this.slashResistance = slashResistance;
        this.penetrationResistance = penetrationResistance;
        this.bluntResistance = bluntResistance;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.baseHealth = baseHealth;
        this.growthRate = growthRate;
        this.disturbed1 = disturbed1;
        this.disturbed2 = disturbed2;
        this.disturbed3 = disturbed3;
        this.seasonType = seasonType;
        this.seasonNumber = seasonNumber;
        this.defenseLevel = defenseLevel;
        this.mentality = mentality;
        this.affiliation = affiliation;
    }

    /**
     * 도메인 객체로부터 JPA 엔티티 생성
     * @param domain Persona 도메인 객체
     * @return PersonaJpa 엔티티
     */
    public static PersonaJpa ofDomain(Persona domain) {
        if (domain == null) {
            return null;
        }

        ResistanceInfo resistanceInfo = domain.getResistanceInfo();
        SpeedInfo speedInfo = domain.getSpeedInfo();
        HealthInfo healthInfo = domain.getHealthInfo();
        SeasonInfo seasonInfo = domain.getSeasonInfo();

        return new PersonaJpa(
            domain.getId(),
            domain.getName(),
            domain.getNameEn(),
            domain.getGrade(),
            domain.getReleaseDate(),
            domain.getMaxLevel(),
            // Resistance
            resistanceInfo != null ? resistanceInfo.getSlashResistance() : ResistanceType.NORMAL,
            resistanceInfo != null ? resistanceInfo.getPenetrationResistance() : ResistanceType.NORMAL,
            resistanceInfo != null ? resistanceInfo.getBluntResistance() : ResistanceType.NORMAL,
            // Speed
            speedInfo != null ? speedInfo.getMinSpeed() : 0,
            speedInfo != null ? speedInfo.getMaxSpeed() : 0,
            // Health
            healthInfo != null ? healthInfo.getBaseHealth() : 0,
            healthInfo != null ? healthInfo.getGrowthRate() : 0.0,
            healthInfo != null ? healthInfo.getDisturbed1() : 0,
            healthInfo != null ? healthInfo.getDisturbed2() : 0,
            healthInfo != null ? healthInfo.getDisturbed3() : 0,
            // Season
            seasonInfo != null ? seasonInfo.getSeasonType() : null,
            seasonInfo != null ? seasonInfo.getNumber() : null,
            // Additional
            domain.getDefenseLevel(),
            domain.getMentality(),
            SubAffiliationJpa.ofDomain(domain.getAffiliation())
        );
    }

    /**
     * 도메인 객체로 기존 엔티티 업데이트
     * @param domain Persona 도메인 객체
     */
    public void updateFromDomain(Persona domain) {
        if (domain == null) {
            return;
        }

        this.name = domain.getName();
        this.nameEn = domain.getNameEn();
        this.grade = domain.getGrade();
        this.releaseDate = domain.getReleaseDate();
        this.maxLevel = domain.getMaxLevel();

        // Resistance
        if (domain.getResistanceInfo() != null) {
            this.slashResistance = domain.getResistanceInfo().getSlashResistance();
            this.penetrationResistance = domain.getResistanceInfo().getPenetrationResistance();
            this.bluntResistance = domain.getResistanceInfo().getBluntResistance();
        }

        // Speed
        if (domain.getSpeedInfo() != null) {
            this.minSpeed = domain.getSpeedInfo().getMinSpeed();
            this.maxSpeed = domain.getSpeedInfo().getMaxSpeed();
        }

        // Health
        if (domain.getHealthInfo() != null) {
            this.baseHealth = domain.getHealthInfo().getBaseHealth();
            this.growthRate = domain.getHealthInfo().getGrowthRate();
            this.disturbed1 = domain.getHealthInfo().getDisturbed1();
            this.disturbed2 = domain.getHealthInfo().getDisturbed2();
            this.disturbed3 = domain.getHealthInfo().getDisturbed3();
        }

        // Season
        if (domain.getSeasonInfo() != null) {
            this.seasonType = domain.getSeasonInfo().getSeasonType();
            this.seasonNumber = domain.getSeasonInfo().getNumber();
        }

        // Additional
        this.defenseLevel = domain.getDefenseLevel();
        this.mentality = domain.getMentality();
        if (domain.getAffiliation() != null) {
            this.affiliation = SubAffiliationJpa.ofDomain(domain.getAffiliation());
        }
    }

    // === 양방향 관계 편의 메서드 ===

    public void setSinner(SinnerJpa sinner) {
        this.sinner = sinner;
    }

    public void addSkill(SkillJpa skill) {
        this.skills.add(skill);
        skill.setPersona(this);
    }

    public void removeSkill(SkillJpa skill) {
        this.skills.remove(skill);
        skill.setPersona(null);
    }

    public void addPassive(PersonaPassiveJpa passive) {
        this.passives.add(passive);
        passive.setPersona(this);
    }

    public void removePassive(PersonaPassiveJpa passive) {
        this.passives.remove(passive);
        passive.setPersona(null);
    }

    public void addImage(PersonaImageJpa image) {
        this.images.add(image);
        image.setPersona(this);
    }

    public void removeImage(PersonaImageJpa image) {
        this.images.remove(image);
        image.setPersona(null);
    }

    public void setAffiliation(SubAffiliationJpa affiliation) {
        this.affiliation = affiliation;
    }
}
