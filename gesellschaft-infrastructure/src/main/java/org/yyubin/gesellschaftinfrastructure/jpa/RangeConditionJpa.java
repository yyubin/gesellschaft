package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.skill.ConditionScope;
import model.skill.ConditionTarget;

/**
 * RangeCondition JPA 엔티티
 * - AbstractCondition을 상속하는 리프 조건
 * - 범위 조건 검사 (예: 5 <= SELF.HP < 10)
 */
@Entity
@Table(name = "range_condition")
@DiscriminatorValue("RANGE")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_range_condition_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RangeConditionJpa extends AbstractConditionJpa {

    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false)
    private ConditionTarget target;

    @Column(name = "stat_code", nullable = false)
    private String statCode;

    @Column(name = "min_inclusive", nullable = false)
    private int minInclusive;

    @Column(name = "max_exclusive")
    private Integer maxExclusive;

    public RangeConditionJpa(ConditionScope scope, ConditionTarget target, String statCode,
                             int minInclusive, Integer maxExclusive) {
        super(scope);
        this.target = target;
        this.statCode = statCode;
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
    }
}
