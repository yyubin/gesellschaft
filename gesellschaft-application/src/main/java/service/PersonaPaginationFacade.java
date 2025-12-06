package service;

import dto.PersonaConnectionResponse;
import dto.PersonaResponse;
import query.GetPersonaByIdQuery;
import query.GetPersonasQuery;
import query.GetPersonasBySinnerQuery;
import query.GetSimilarPersonasQuery;

import java.util.List;

public interface PersonaPaginationFacade {

    /**
     * Get a single persona by ID
     */
    PersonaResponse getPersona(GetPersonaByIdQuery query);

    /**
     * Get paginated personas with cursor-based pagination
     */
    PersonaConnectionResponse getPersonas(GetPersonasQuery query);

    /**
     * Get personas by sinner ID
     */
    List<PersonaResponse> getPersonasBySinner(GetPersonasBySinnerQuery query);

    List<PersonaResponse> getSimilarPersonas(GetSimilarPersonasQuery query);
}
