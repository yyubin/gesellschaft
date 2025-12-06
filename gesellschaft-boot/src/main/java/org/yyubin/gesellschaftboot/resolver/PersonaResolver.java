package org.yyubin.gesellschaftboot.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.yyubin.gesellschaftboot.dto.PersonaResponseDto;
import org.yyubin.gesellschaftboot.dto.pagination.PageInfo;
import org.yyubin.gesellschaftboot.dto.pagination.PersonaConnection;
import org.yyubin.gesellschaftboot.dto.pagination.PersonaEdge;
import org.yyubin.gesellschaftboot.util.CursorUtil;
import service.PersonaService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PersonaResolver {

    private final PersonaService personaService;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @QueryMapping
    public PersonaResponseDto persona(@Argument Long id) {
        var response = personaService.getPersonaById(id);
        return PersonaResponseDto.from(response);
    }

    @QueryMapping
    public PersonaConnection personas(
        @Argument Integer first,
        @Argument String after,
        @Argument Integer last,
        @Argument String before
    ) {
        if (first != null && last != null) {
            throw new IllegalArgumentException("Cannot use both 'first' and 'last' arguments");
        }
        if (after != null && before != null) {
            throw new IllegalArgumentException("Cannot use both 'after' and 'before' cursors");
        }

        // Determine page size and direction
        boolean isForward = first != null || after != null;
        int pageSize = first != null ? first : (last != null ? last : DEFAULT_PAGE_SIZE);

        // Decode cursors
        Long afterId = after != null ? CursorUtil.decode(after) : null;
        Long beforeId = before != null ? CursorUtil.decode(before) : null;

        // Fetch data (fetch one more to determine if there's a next/previous page)
        var responses = personaService.getPersonasWithCursor(afterId, beforeId, pageSize + 1);

        // Check if there are more items
        boolean hasMore = responses.size() > pageSize;
        if (hasMore) {
            responses = responses.subList(0, pageSize);
        }

        if (!isForward && beforeId != null) {
            responses = responses.reversed();
        }

        List<PersonaEdge> edges = responses.stream()
            .map(response -> {
                PersonaResponseDto dto = PersonaResponseDto.from(response);
                String cursor = CursorUtil.encode(response.id());
                return PersonaEdge.of(dto, cursor);
            })
            .toList();

        String startCursor = edges.isEmpty() ? null : edges.get(0).cursor();
        String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).cursor();

        boolean hasNextPage = isForward ? hasMore : (afterId != null || beforeId != null);
        boolean hasPreviousPage = !isForward ? hasMore : (afterId != null);

        PageInfo pageInfo = PageInfo.of(hasNextPage, hasPreviousPage, startCursor, endCursor);

        long totalCount = personaService.countPersonas();

        return PersonaConnection.of(edges, pageInfo, (int) totalCount);
    }

    @QueryMapping
    public List<PersonaResponseDto> personasBySinner(@Argument Long sinnerId) {
        var responses = personaService.getPersonasBySinnerId(sinnerId);
        return responses.stream()
            .map(PersonaResponseDto::from)
            .toList();
    }
}
