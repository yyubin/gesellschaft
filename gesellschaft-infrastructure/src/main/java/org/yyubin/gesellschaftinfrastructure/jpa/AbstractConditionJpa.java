package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.skill.ConditionScope;

/**
 * AbstractCondition JPA 엔티티 추상 클래스
 * - JOINED 전략으로 ConditionGroup, StatCondition, RangeCondition 계층 구현
 */
@Entity
@Table(name = "abstract_condition")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "condition_type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbstractConditionJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private ConditionScope scope;

    protected AbstractConditionJpa(ConditionScope scope) {
        this.scope = scope;
    }
}
