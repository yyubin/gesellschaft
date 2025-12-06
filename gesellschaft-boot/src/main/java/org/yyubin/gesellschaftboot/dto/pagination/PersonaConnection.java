package org.yyubin.gesellschaftboot.dto.pagination;

import java.util.List;

public record PersonaConnection(
    List<PersonaEdge> edges,
    PageInfo pageInfo,
    Integer totalCount
) {
    public static PersonaConnection of(List<PersonaEdge> edges, PageInfo pageInfo, Integer totalCount) {
        return new PersonaConnection(edges, pageInfo, totalCount);
    }
}
