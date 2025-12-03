package org.yyubin.gesellschaftinfrastructure.mapper;

import model.SubAffiliation;
import model.persona.*;
import org.springframework.stereotype.Component;
import org.yyubin.gesellschaftinfrastructure.jpa.PersonaJpa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Persona POJO ↔ PersonaJpa 매퍼
 * - 헥사고날 아키텍처: 도메인과 인프라 계층 분리
 * - 양방향 변환 지원
 * - Value Object 변환 처리
 */
@Component
public class PersonaMapper {

    private final SkillMapper skillMapper;
    private final PassiveMapper passiveMapper;
    private final ImageMapper imageMapper;

    public PersonaMapper(SkillMapper skillMapper, PassiveMapper passiveMapper, ImageMapper imageMapper) {
        this.skillMapper = skillMapper;
        this.passiveMapper = passiveMapper;
        this.imageMapper = imageMapper;
    }

    /**
     * JPA → POJO 도메인 변환
     * @param jpa PersonaJpa 엔티티
     * @return Persona 도메인 객체
     */
    public Persona toDomain(PersonaJpa jpa) {
        if (jpa == null) {
            return null;
        }

        // Value Objects 재구성
        ResistanceInfo resistanceInfo = new ResistanceInfo(
            jpa.getSlashResistance(),
            jpa.getPenetrationResistance(),
            jpa.getBluntResistance()
        );

        SpeedInfo speedInfo = new SpeedInfo(
            jpa.getMinSpeed(),
            jpa.getMaxSpeed()
        );

        HealthInfo healthInfo = new HealthInfo(
            jpa.getBaseHealth(),
            jpa.getGrowthRate(),
            jpa.getDisturbed1(),
            jpa.getDisturbed2(),
            jpa.getDisturbed3()
        );

        SeasonInfo seasonInfo = jpa.getSeasonType() != null
            ? new SeasonInfo(jpa.getSeasonType(), jpa.getSeasonNumber())
            : null;

        // SubAffiliation 변환
        SubAffiliation affiliation = jpa.getAffiliation() != null
            ? jpa.getAffiliation().toDomain()
            : null;

        // 도메인 객체 생성
        return Persona.create(
            jpa.getName(),
            jpa.getNameEn(),
            jpa.getGrade(),
            jpa.getReleaseDate(),
            jpa.getMaxLevel(),
            resistanceInfo,
            speedInfo,
            healthInfo,
            seasonInfo,
            jpa.getDefenseLevel(),
            jpa.getMentality(),
            affiliation,
            jpa.getSkills() != null
                ? jpa.getSkills().stream().map(skillMapper::toDomain).toList()
                : List.of(),
            jpa.getPassives() != null
                ? jpa.getPassives().stream().map(passiveMapper::toDomain).toList()
                : List.of(),
            jpa.getImages() != null
                ? jpa.getImages().stream().map(imageMapper::toDomain).toList()
                : List.of()
        ).withId(jpa.getId());
    }

    /**
     * POJO 도메인 → JPA 변환
     * @param domain Persona 도메인 객체
     * @return PersonaJpa 엔티티
     */
    public PersonaJpa toJpa(Persona domain) {
        if (domain == null) {
            return null;
        }

        // 팩토리 메서드로 JPA 엔티티 생성
        PersonaJpa jpa = PersonaJpa.ofDomain(domain);

        // 자식 엔티티 변환 및 양방향 관계 설정
        if (domain.getSkills() != null && !domain.getSkills().isEmpty()) {
            domain.getSkills().stream()
                .map(skillMapper::toJpa)
                .forEach(jpa::addSkill);
        }

        if (domain.getPassives() != null && !domain.getPassives().isEmpty()) {
            domain.getPassives().stream()
                .map(passiveMapper::toJpa)
                .forEach(jpa::addPassive);
        }

        if (domain.getImages() != null && !domain.getImages().isEmpty()) {
            domain.getImages().stream()
                .map(imageMapper::toJpa)
                .forEach(jpa::addImage);
        }

        return jpa;
    }

    /**
     * 기존 JPA 엔티티를 도메인 객체로 업데이트
     * @param jpa 기존 JPA 엔티티
     * @param domain 업데이트할 도메인 객체
     */
    public void updateJpaFromDomain(PersonaJpa jpa, Persona domain) {
        if (jpa == null || domain == null) {
            return;
        }

        // 기본 필드 및 Value Objects 업데이트
        jpa.updateFromDomain(domain);

        // Skills 동기화
        jpa.getSkills().clear();
        if (domain.getSkills() != null) {
            domain.getSkills().stream()
                .map(skillMapper::toJpa)
                .forEach(jpa::addSkill);
        }

        // Passives 동기화
        jpa.getPassives().clear();
        if (domain.getPassives() != null) {
            domain.getPassives().stream()
                .map(passiveMapper::toJpa)
                .forEach(jpa::addPassive);
        }

        // Images 동기화
        jpa.getImages().clear();
        if (domain.getImages() != null) {
            domain.getImages().stream()
                .map(imageMapper::toJpa)
                .forEach(jpa::addImage);
        }
    }

    /**
     * 리스트 변환: JPA → 도메인
     */
    public List<Persona> toDomainList(List<PersonaJpa> jpaList) {
        if (jpaList == null) {
            return List.of();
        }
        return jpaList.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * 리스트 변환: 도메인 → JPA
     */
    public List<PersonaJpa> toJpaList(List<Persona> domainList) {
        if (domainList == null) {
            return List.of();
        }
        return domainList.stream()
            .map(this::toJpa)
            .collect(Collectors.toList());
    }
}
