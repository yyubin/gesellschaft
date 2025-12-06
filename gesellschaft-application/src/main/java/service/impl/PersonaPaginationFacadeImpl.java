package service.impl;

import dto.PersonaConnectionResponse;
import dto.PersonaResponse;
import lombok.RequiredArgsConstructor;
import query.GetPersonaByIdQuery;
import query.GetPersonasQuery;
import query.GetPersonasBySinnerQuery;
import query.GetSimilarPersonasQuery;
import service.PersonaPaginationFacade;
import service.PersonaRecommendationService;
import service.PersonaService;

import java.util.List;

@RequiredArgsConstructor
public class PersonaPaginationFacadeImpl implements PersonaPaginationFacade {

    private final PersonaService personaService;
    private final PersonaRecommendationService recommendationService;

    @Override
    public PersonaResponse getPersona(GetPersonaByIdQuery query) {
        return personaService.getPersona(query);
    }

    @Override
    public PersonaConnectionResponse getPersonas(GetPersonasQuery query) {
        return personaService.getPersonas(query);
    }

    @Override
    public List<PersonaResponse> getPersonasBySinner(GetPersonasBySinnerQuery query) {
        return personaService.getPersonasBySinner(query);
    }

    @Override
    public List<PersonaResponse> getSimilarPersonas(GetSimilarPersonasQuery query) {
        return recommendationService.getSimilarPersonas(query);
    }
}
