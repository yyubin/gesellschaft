package org.yyubin.gesellschaftinfrastructure.mapper;

import model.passive.PassiveCondition;
import model.passive.PersonaPassive;
import org.springframework.stereotype.Component;
import org.yyubin.gesellschaftinfrastructure.jpa.PersonaPassiveJpa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PersonaPassive POJO ↔ PersonaPassiveJpa 매퍼
 */
@Component
public class PassiveMapper {

    // TODO: PassiveEffect 매퍼는 추후 구현
    // private final PassiveEffectMapper passiveEffectMapper;

    /**
     * JPA → POJO 도메인 변환
     */
    public PersonaPassive toDomain(PersonaPassiveJpa jpa) {
        if (jpa == null) {
            return null;
        }

        // PassiveCondition 재구성 (nullable)
        PassiveCondition condition = null;
        if (jpa.getConditionSinAffinity() != null && jpa.getConditionType() != null) {
            condition = PassiveCondition.of(
                jpa.getConditionSinAffinity(),
                jpa.getConditionType(),
                jpa.getConditionCount() != null ? jpa.getConditionCount() : 0
            );
        }

        // TODO: PassiveEffect 변환
        // PassiveEffect effect = passiveEffectMapper.toDomain(jpa.getEffect());
        // 임시로 더미 PassiveEffect 생성 (실제 구현 필요)
        model.passive.PassiveEffect dummyEffect = createDummyEffect();

        return PersonaPassive.builder(jpa.getName(), jpa.getKind())
            .syncLevel(jpa.getSyncLevel())
            .condition(condition)
            .effect(dummyEffect)  // TODO: PassiveEffect 구현 후 실제 매핑
            .build()
            .withId(jpa.getId());
    }

    // 임시 더미 PassiveEffect 생성 (컴파일 에러 방지)
    private model.passive.PassiveEffect createDummyEffect() {
        // TODO: 실제 PassiveEffect 매핑 구현 필요
        return null; // 일단 null 반환 (추후 구현)
    }

    /**
     * POJO 도메인 → JPA 변환
     */
    public PersonaPassiveJpa toJpa(PersonaPassive domain) {
        if (domain == null) {
            return null;
        }

        PersonaPassiveJpa jpa = PersonaPassiveJpa.ofDomain(domain);

        // TODO: PassiveEffect 변환
        // if (domain.getEffect() != null) {
        //     PassiveEffectJpa effectJpa = passiveEffectMapper.toJpa(domain.getEffect());
        //     jpa.setEffect(effectJpa);
        // }

        return jpa;
    }

    /**
     * 기존 JPA 엔티티 업데이트
     */
    public void updateJpaFromDomain(PersonaPassiveJpa jpa, PersonaPassive domain) {
        if (jpa == null || domain == null) {
            return;
        }

        jpa.updateFromDomain(domain);

        // TODO: PassiveEffect 업데이트
        // if (domain.getEffect() != null) {
        //     if (jpa.getEffect() != null) {
        //         passiveEffectMapper.updateJpaFromDomain(jpa.getEffect(), domain.getEffect());
        //     } else {
        //         PassiveEffectJpa effectJpa = passiveEffectMapper.toJpa(domain.getEffect());
        //         jpa.setEffect(effectJpa);
        //     }
        // }
    }

    /**
     * 리스트 변환: JPA → 도메인
     */
    public List<PersonaPassive> toDomainList(List<PersonaPassiveJpa> jpaList) {
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
    public List<PersonaPassiveJpa> toJpaList(List<PersonaPassive> domainList) {
        if (domainList == null) {
            return List.of();
        }
        return domainList.stream()
            .map(this::toJpa)
            .collect(Collectors.toList());
    }
}
