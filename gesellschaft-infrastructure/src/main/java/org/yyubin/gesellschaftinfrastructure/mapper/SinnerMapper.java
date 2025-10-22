package org.yyubin.gesellschaftinfrastructure.mapper;

import model.Sinner;
import org.springframework.stereotype.Component;
import org.yyubin.gesellschaftinfrastructure.jpa.SinnerJpa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sinner POJO ↔ SinnerJpa 매퍼
 * - 헥사고날 아키텍처: 도메인과 인프라 계층 분리
 * - 양방향 변환 지원
 */
@Component
public class SinnerMapper {

    private final PersonaMapper personaMapper;

    public SinnerMapper(PersonaMapper personaMapper) {
        this.personaMapper = personaMapper;
    }

    /**
     * JPA → POJO 도메인 변환
     * @param jpa SinnerJpa 엔티티
     * @return Sinner 도메인 객체
     */
    public Sinner toDomain(SinnerJpa jpa) {
        if (jpa == null) {
            return null;
        }

        return new Sinner(
            jpa.getId(),
            jpa.getName(),
            jpa.getNameEn(),
            jpa.getPersonas() != null
                ? jpa.getPersonas().stream()
                    .map(personaMapper::toDomain)
                    .collect(Collectors.toList())
                : List.of()
        );
    }

    /**
     * POJO 도메인 → JPA 변환
     * @param domain Sinner 도메인 객체
     * @return SinnerJpa 엔티티
     */
    public SinnerJpa toJpa(Sinner domain) {
        if (domain == null) {
            return null;
        }

        // 팩토리 메서드로 JPA 엔티티 생성
        SinnerJpa jpa = SinnerJpa.ofDomain(domain);

        // Persona 변환 및 양방향 관계 설정
        if (domain.getPersonas() != null && !domain.getPersonas().isEmpty()) {
            domain.getPersonas().stream()
                .map(personaMapper::toJpa)
                .forEach(jpa::addPersona);  // 양방향 관계 자동 동기화
        }

        return jpa;
    }

    /**
     * 기존 JPA 엔티티를 도메인 객체로 업데이트
     * @param jpa 기존 JPA 엔티티
     * @param domain 업데이트할 도메인 객체
     */
    public void updateJpaFromDomain(SinnerJpa jpa, Sinner domain) {
        if (jpa == null || domain == null) {
            return;
        }

        // 기본 필드 업데이트
        jpa.updateFromDomain(domain);

        // Persona 리스트 동기화
        jpa.getPersonas().clear();
        if (domain.getPersonas() != null) {
            domain.getPersonas().stream()
                .map(personaMapper::toJpa)
                .forEach(jpa::addPersona);  // 양방향 관계 자동 동기화
        }
    }

    /**
     * 리스트 변환: JPA → 도메인
     */
    public List<Sinner> toDomainList(List<SinnerJpa> jpaList) {
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
    public List<SinnerJpa> toJpaList(List<Sinner> domainList) {
        if (domainList == null) {
            return List.of();
        }
        return domainList.stream()
            .map(this::toJpa)
            .collect(Collectors.toList());
    }
}
