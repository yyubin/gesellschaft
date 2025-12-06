package org.yyubin.gesellschaftneo4j.node;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("SinAffinity")
@Getter
@Setter
@NoArgsConstructor
public class SinAffinityNode {

    @Id
    @GeneratedValue
    private Long id;

    private String type;  // WRATH, LUST, SLOTH, GREED, GLOOM, PRIDE, ENVY
    private String name;  // Korean name

    @Builder
    public SinAffinityNode(Long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}
