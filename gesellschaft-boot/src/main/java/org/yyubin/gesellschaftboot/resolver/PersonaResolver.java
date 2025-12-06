package org.yyubin.gesellschaftboot.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.yyubin.gesellschaftboot.converter.PersonaConnectionConverter;
import org.yyubin.gesellschaftboot.dto.PersonaResponseDto;
import org.yyubin.gesellschaftboot.dto.input.PaginationInput;
import org.yyubin.gesellschaftboot.dto.pagination.PersonaConnection;
import query.GetPersonaByIdQuery;
import query.GetPersonasQuery;
import query.GetPersonasBySinnerQuery;
import query.GetSimilarPersonasQuery;
import service.PersonaPaginationFacade;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PersonaResolver {

    private final PersonaPaginationFacade facade;
    private final PersonaConnectionConverter converter;

    @QueryMapping
    public PersonaResponseDto persona(@Argument Long id) {
        var query = new GetPersonaByIdQuery(id);
        var response = facade.getPersona(query);
        return PersonaResponseDto.from(response);
    }

    @QueryMapping
    public PersonaConnection personas(@Argument PaginationInput input) {
        var query = new GetPersonasQuery(
            input.getFirst(),
            input.getAfter(),
            input.getLast(),
            input.getBefore()
        );
        var response = facade.getPersonas(query);
        return converter.toGraphQL(response);
    }

    @QueryMapping
    public List<PersonaResponseDto> personasBySinner(@Argument Long sinnerId) {
        var query = new GetPersonasBySinnerQuery(sinnerId);
        var responses = facade.getPersonasBySinner(query);
        return responses.stream()
            .map(PersonaResponseDto::from)
            .toList();
    }

    @QueryMapping
    public List<PersonaResponseDto> similarPersonas(@Argument Long personaId, @Argument Integer limit) {
        var query = new GetSimilarPersonasQuery(personaId, limit);
        var responses = facade.getSimilarPersonas(query);
        return responses.stream()
            .map(PersonaResponseDto::from)
            .toList();
    }
}
