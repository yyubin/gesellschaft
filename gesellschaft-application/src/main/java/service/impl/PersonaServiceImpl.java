package service.impl;

import dto.*;
import exception.PersonaNotFoundException;
import lombok.RequiredArgsConstructor;
import model.persona.Persona;
import port.PersonaRecommendationRepository;
import port.PersonaRepository;
import query.GetPersonaByIdQuery;
import query.GetPersonasQuery;
import query.GetPersonasBySinnerQuery;
import query.GetSimilarPersonasQuery;
import service.PersonaService;
import service.SinnerService;
import util.CursorUtil;

import java.util.List;

@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final SinnerService sinnerService;

    @Override
    public PersonaResponse getPersona(GetPersonaByIdQuery query) {
        Persona persona = personaRepository.findById(query.id())
            .orElseThrow(() -> new PersonaNotFoundException(query.id()));
        return PersonaResponse.from(persona, null);
    }

    @Override
    public PersonaConnectionResponse getPersonas(GetPersonasQuery query) {
        // Decode cursors
        Long afterId = CursorUtil.decode(query.after());
        Long beforeId = CursorUtil.decode(query.before());

        // Fetch data
        List<Persona> personas = personaRepository.findAllWithCursor(afterId, beforeId, query.getPageSize());

        // Check if there are more items
        boolean hasMore = personas.size() > query.getActualLimit();
        if (hasMore) {
            personas = personas.subList(0, query.getActualLimit());
        }

        // Reverse if backward pagination
        if (!query.isForward() && beforeId != null) {
            personas = personas.reversed();
        }

        // Convert to responses
        List<PersonaResponse> personaResponses = personas.stream()
            .map(persona -> PersonaResponse.from(persona, null))
            .toList();

        // Create edges
        List<PersonaEdgeResponse> edges = personaResponses.stream()
            .map(response -> {
                String cursor = CursorUtil.encode(response.id());
                return PersonaEdgeResponse.of(response, cursor);
            })
            .toList();

        // Create page info
        String startCursor = edges.isEmpty() ? null : edges.get(0).cursor();
        String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).cursor();

        boolean hasNextPage = query.isForward() ? hasMore : (afterId != null || beforeId != null);
        boolean hasPreviousPage = !query.isForward() ? hasMore : (afterId != null);

        PageInfoResponse pageInfo = PageInfoResponse.of(hasNextPage, hasPreviousPage, startCursor, endCursor);

        // Get total count
        long totalCount = personaRepository.count();

        return PersonaConnectionResponse.of(edges, pageInfo, (int) totalCount);
    }

    @Override
    public List<PersonaResponse> getPersonasBySinner(GetPersonasBySinnerQuery query) {
        List<Persona> personas = personaRepository.findBySinnerId(query.sinnerId());

        SinnerResponse sinnerResponse = sinnerService.getSinnerByIdOrNull(query.sinnerId());

        return personas.stream()
            .map(persona -> PersonaResponse.from(persona, sinnerResponse))
            .toList();
    }

    // Legacy methods
    @Override
    @Deprecated(forRemoval = true)
    public PersonaResponse getPersonaById(Long id) {
        return getPersona(new GetPersonaByIdQuery(id));
    }

    @Override
    @Deprecated(forRemoval = true)
    public List<PersonaResponse> getAllPersonas() {
        List<Persona> personas = personaRepository.findAll();
        return personas.stream()
            .map(persona -> PersonaResponse.from(persona, null))
            .toList();
    }

    @Override
    @Deprecated(forRemoval = true)
    public List<PersonaResponse> getPersonasBySinnerId(Long sinnerId) {
        return getPersonasBySinner(new GetPersonasBySinnerQuery(sinnerId));
    }
}
