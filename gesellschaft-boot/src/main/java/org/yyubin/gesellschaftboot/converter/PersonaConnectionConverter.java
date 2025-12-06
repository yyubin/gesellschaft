package org.yyubin.gesellschaftboot.converter;

import dto.PersonaConnectionResponse;
import org.springframework.stereotype.Component;
import org.yyubin.gesellschaftboot.dto.PersonaResponseDto;
import org.yyubin.gesellschaftboot.dto.pagination.PageInfo;
import org.yyubin.gesellschaftboot.dto.pagination.PersonaConnection;
import org.yyubin.gesellschaftboot.dto.pagination.PersonaEdge;

import java.util.List;

@Component
public class PersonaConnectionConverter {

    public PersonaConnection toGraphQL(PersonaConnectionResponse response) {
        if (response == null) {
            return null;
        }

        // Convert edges
        List<PersonaEdge> edges = response.edges().stream()
            .map(edge -> PersonaEdge.of(
                PersonaResponseDto.from(edge.node()),
                edge.cursor()
            ))
            .toList();

        // Convert page info
        PageInfo pageInfo = PageInfo.of(
            response.pageInfo().hasNextPage(),
            response.pageInfo().hasPreviousPage(),
            response.pageInfo().startCursor(),
            response.pageInfo().endCursor()
        );

        return PersonaConnection.of(edges, pageInfo, response.totalCount());
    }
}
