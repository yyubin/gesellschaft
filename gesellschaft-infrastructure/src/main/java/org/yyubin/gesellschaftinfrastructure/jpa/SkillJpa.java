package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.AttackType;
import model.KeywordType;
import model.SinAffinity;
import model.skill.DefenseType;
import model.skill.Skill;
import model.skill.SkillCategoryType;

import java.util.ArrayList;
import java.util.List;

/**
 * Skill JPA 엔티티
 * - 인격의 개별 스킬
 * - 동기화 레벨별 스탯 포함 (SkillStatsBySyncJpa)
 */
@Entity
@Table(name = "skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SkillJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int skillNumber;  // 1, 2, 3

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SkillCategoryType skillCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SinAffinity sinAffinity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private KeywordType keywordType;

    @Column
    private Integer skillQuantity;  // nullable (방어 스킬은 없음)

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AttackType attackType;  // nullable

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DefenseType defenseType;  // nullable

    @Column(length = 500)
    private String skillImage;  // nullable

    // 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private PersonaJpa persona;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillStatsBySyncJpa> statsBySync = new ArrayList<>();

    @Builder
    public SkillJpa(int skillNumber, String name, SkillCategoryType skillCategory,
                    SinAffinity sinAffinity, KeywordType keywordType, Integer skillQuantity,
                    AttackType attackType, DefenseType defenseType, String skillImage) {
        this.skillNumber = skillNumber;
        this.name = name;
        this.skillCategory = skillCategory;
        this.sinAffinity = sinAffinity;
        this.keywordType = keywordType;
        this.skillQuantity = skillQuantity;
        this.attackType = attackType;
        this.defenseType = defenseType;
        this.skillImage = skillImage;
    }

    // ID를 포함한 전체 생성자 (매퍼 전용)
    public SkillJpa(Long id, int skillNumber, String name, SkillCategoryType skillCategory,
                    SinAffinity sinAffinity, KeywordType keywordType, Integer skillQuantity,
                    AttackType attackType, DefenseType defenseType, String skillImage) {
        this.id = id;
        this.skillNumber = skillNumber;
        this.name = name;
        this.skillCategory = skillCategory;
        this.sinAffinity = sinAffinity;
        this.keywordType = keywordType;
        this.skillQuantity = skillQuantity;
        this.attackType = attackType;
        this.defenseType = defenseType;
        this.skillImage = skillImage;
    }

    /**
     * 도메인 객체로부터 JPA 엔티티 생성
     */
    public static SkillJpa ofDomain(Skill domain) {
        if (domain == null) {
            return null;
        }
        return new SkillJpa(
            domain.getId(),
            domain.getSkillNumber(),
            domain.getName(),
            domain.getSkillCategory(),
            domain.getSinAffinity(),
            domain.getKeywordType(),
            domain.getSkillQuantity(),
            domain.getAttackType(),
            domain.getDefenseType(),
            domain.getSkillImage()
        );
    }

    /**
     * 도메인 객체로 기존 엔티티 업데이트
     */
    public void updateFromDomain(Skill domain) {
        if (domain == null) {
            return;
        }
        this.skillNumber = domain.getSkillNumber();
        this.name = domain.getName();
        this.skillCategory = domain.getSkillCategory();
        this.sinAffinity = domain.getSinAffinity();
        this.keywordType = domain.getKeywordType();
        this.skillQuantity = domain.getSkillQuantity();
        this.attackType = domain.getAttackType();
        this.defenseType = domain.getDefenseType();
        this.skillImage = domain.getSkillImage();
    }

    // 양방향 관계 편의 메서드
    public void setPersona(PersonaJpa persona) {
        this.persona = persona;
    }

    public void addStatsBySync(SkillStatsBySyncJpa stats) {
        this.statsBySync.add(stats);
        stats.setSkill(this);
    }

    public void removeStatsBySync(SkillStatsBySyncJpa stats) {
        this.statsBySync.remove(stats);
        stats.setSkill(null);
    }
}
