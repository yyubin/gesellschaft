package service.impl;

import dto.PersonaResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import model.persona.Persona;
import port.PersonaRecommendationRepository;
import query.GetSimilarPersonasQuery;
import service.PersonaRecommendationService;

@RequiredArgsConstructor
public class PersonaRecommendationServiceImpl implements PersonaRecommendationService {

    private final PersonaRecommendationRepository recommendationRepository;

    @Override
    public List<PersonaResponse> getSimilarPersonas(GetSimilarPersonasQuery query) {
        if (recommendationRepository == null) {
            throw new UnsupportedOperationException("Persona recommendation is not available (Neo4j not configured)");
        }

        List<Persona> personas = recommendationRepository.findSimilarPersonas(query.personaId(), query.getLimit());

        return personas.stream()
                .map(persona -> PersonaResponse.from(persona, null))
                .toList();
    }

}
