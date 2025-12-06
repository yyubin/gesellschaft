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

@Node("Coin")
@Getter
@Setter
@NoArgsConstructor
public class CoinNode {

    @Id
    @GeneratedValue
    private Long id;

    private Integer number;
    private String type;        // NORMAL, etc
    private String description;

    @Relationship(type = "HAS_EFFECT", direction = Relationship.Direction.OUTGOING)
    private List<EffectNode> effects = new ArrayList<>();

    @Builder
    public CoinNode(Long id, Integer number, String type, String description, List<EffectNode> effects) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.description = description;
        this.effects = effects != null ? effects : new ArrayList<>();
    }
}
