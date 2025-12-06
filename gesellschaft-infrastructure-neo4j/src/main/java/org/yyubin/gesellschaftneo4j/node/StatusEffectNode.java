package org.yyubin.gesellschaftneo4j.node;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("StatusEffect")
@Getter
@Setter
@NoArgsConstructor
public class StatusEffectNode {

    @Id
    @GeneratedValue
    private Long id;

    private String type;  // RUPTURE, PARALYSIS, VULNERABLE, PIERCE_VULNERABLE, etc
    private String name;  // Display name

    @Builder
    public StatusEffectNode(Long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}
