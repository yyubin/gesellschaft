package org.yyubin.gesellschaftboot.dto.pagination;

import org.yyubin.gesellschaftboot.dto.PersonaResponseDto;

public record PersonaEdge(
    PersonaResponseDto node,
    String cursor
) {
    public static PersonaEdge of(PersonaResponseDto node, String cursor) {
        return new PersonaEdge(node, cursor);
    }
}
