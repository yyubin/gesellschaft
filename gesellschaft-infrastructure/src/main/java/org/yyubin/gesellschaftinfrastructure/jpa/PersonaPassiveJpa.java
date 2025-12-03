package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.SinAffinity;
import model.passive.ConditionType;
import model.passive.PassiveKind;
import model.passive.PersonaPassive;
import model.skill.SyncLevel;

/**
 * PersonaPassive JPA 엔티티
 * - 인격 패시브 (일반 / 서포트)
 * - PassiveCondition은 Embedded 방식으로 평탄화
 */
@Entity
@Table(name = "persona_passive")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PersonaPassiveJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PassiveKind kind;  // NORMAL / SUPPORT

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private SyncLevel syncLevel;  // nullable (SUPPORT일 때만)

    // PassiveCondition 필드들 (Embedded - 평탄화)
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_sin_affinity", length = 20)
    private SinAffinity conditionSinAffinity;  // nullable

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", length = 20)
    private ConditionType conditionType;  // nullable

    @Column(name = "condition_count")
    private Integer conditionCount;  // nullable

    // 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private PersonaJpa persona;

    @OneToOne(mappedBy = "personaPassive", cascade = CascadeType.ALL, orphanRemoval = true)
    private PassiveEffectJpa effect;

    @Builder
    public PersonaPassiveJpa(String name, PassiveKind kind, SyncLevel syncLevel,
                            SinAffinity conditionSinAffinity, ConditionType conditionType,
                            Integer conditionCount) {
        this.name = name;
        this.kind = kind;
        this.syncLevel = syncLevel;
        this.conditionSinAffinity = conditionSinAffinity;
        this.conditionType = conditionType;
        this.conditionCount = conditionCount;
    }

    // ID를 포함한 전체 생성자 (매퍼 전용)
    public PersonaPassiveJpa(Long id, String name, PassiveKind kind, SyncLevel syncLevel,
                            SinAffinity conditionSinAffinity, ConditionType conditionType,
                            Integer conditionCount) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.syncLevel = syncLevel;
        this.conditionSinAffinity = conditionSinAffinity;
        this.conditionType = conditionType;
        this.conditionCount = conditionCount;
    }

    /**
     * 도메인 객체로부터 JPA 엔티티 생성
     */
    public static PersonaPassiveJpa ofDomain(PersonaPassive domain) {
        if (domain == null) {
            return null;
        }

        return new PersonaPassiveJpa(
            domain.getId(),
            domain.getName(),
            domain.getKind(),
            domain.getSyncLevel().orElse(null),
            domain.getCondition().map(c -> c.getSinAffinity()).orElse(null),
            domain.getCondition().map(c -> c.getConditionType()).orElse(null),
            domain.getCondition().map(c -> c.getCount()).orElse(null)
        );
    }

    /**
     * 도메인 객체로 기존 엔티티 업데이트
     */
    public void updateFromDomain(PersonaPassive domain) {
        if (domain == null) {
            return;
        }
        this.name = domain.getName();
        this.kind = domain.getKind();
        this.syncLevel = domain.getSyncLevel().orElse(null);
        this.conditionSinAffinity = domain.getCondition().map(c -> c.getSinAffinity()).orElse(null);
        this.conditionType = domain.getCondition().map(c -> c.getConditionType()).orElse(null);
        this.conditionCount = domain.getCondition().map(c -> c.getCount()).orElse(null);
    }

    // 양방향 관계 편의 메서드
    public void setPersona(PersonaJpa persona) {
        this.persona = persona;
    }

    public void setEffect(PassiveEffectJpa effect) {
        this.effect = effect;
        if (effect != null) {
            effect.setPersonaPassive(this);
        }
    }
}
