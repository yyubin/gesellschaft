package org.yyubin.gesellschaftinfrastructure.mapper;

import model.persona.PersonaImage;
import org.springframework.stereotype.Component;
import org.yyubin.gesellschaftinfrastructure.jpa.PersonaImageJpa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PersonaImage POJO ↔ PersonaImageJpa 매퍼
 */
@Component
public class ImageMapper {

    /**
     * JPA → POJO 도메인 변환
     */
    public PersonaImage toDomain(PersonaImageJpa jpa) {
        if (jpa == null) {
            return null;
        }

        return PersonaImage.builder(jpa.getType(), jpa.getUrl())
            .priority(jpa.getPriority())
            .isPrimary(jpa.isPrimary())
            .build()
            .withId(jpa.getId());
    }

    /**
     * POJO 도메인 → JPA 변환
     */
    public PersonaImageJpa toJpa(PersonaImage domain) {
        if (domain == null) {
            return null;
        }
        return PersonaImageJpa.ofDomain(domain);
    }

    /**
     * 기존 JPA 엔티티 업데이트
     */
    public void updateJpaFromDomain(PersonaImageJpa jpa, PersonaImage domain) {
        if (jpa == null || domain == null) {
            return;
        }
        jpa.updateFromDomain(domain);
    }

    /**
     * 리스트 변환: JPA → 도메인
     */
    public List<PersonaImage> toDomainList(List<PersonaImageJpa> jpaList) {
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
    public List<PersonaImageJpa> toJpaList(List<PersonaImage> domainList) {
        if (domainList == null) {
            return List.of();
        }
        return domainList.stream()
            .map(this::toJpa)
            .collect(Collectors.toList());
    }
}
