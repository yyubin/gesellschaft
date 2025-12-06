package org.yyubin.gesellschaftneo4j.node;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Passive")
@Getter
@Setter
@NoArgsConstructor
public class PassiveNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String kind;  // NORMAL, SUPPORT

    // Activation condition
    private String conditionType;   // HOLD, RESONATE
    private String conditionSin;    // SinAffinity type
    private Integer conditionCount;

    // Relationships
    @Relationship(type = "HAS_EFFECT", direction = Relationship.Direction.OUTGOING)
    private List<EffectNode> effects = new ArrayList<>();

    @Relationship(type = "REQUIRES_SIN", direction = Relationship.Direction.OUTGOING)
    private SinAffinityNode requiredSin;

    @Builder
    public PassiveNode(Long id, String name, String kind,
                       String conditionType, String conditionSin, Integer conditionCount,
                       List<EffectNode> effects, SinAffinityNode requiredSin) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.conditionType = conditionType;
        this.conditionSin = conditionSin;
        this.conditionCount = conditionCount;
        this.effects = effects != null ? effects : new ArrayList<>();
        this.requiredSin = requiredSin;
    }
}
