package service;

import dto.PersonaConnectionResponse;
import dto.PersonaResponse;
import query.GetPersonaByIdQuery;
import query.GetPersonasQuery;
import query.GetPersonasBySinnerQuery;
import query.GetSimilarPersonasQuery;

import java.util.List;

public interface PersonaService {

    // Query-based methods
    PersonaResponse getPersona(GetPersonaByIdQuery query);
    PersonaConnectionResponse getPersonas(GetPersonasQuery query);
    List<PersonaResponse> getPersonasBySinner(GetPersonasBySinnerQuery query);

    // Legacy methods (deprecated)
    @Deprecated(forRemoval = true)
    PersonaResponse getPersonaById(Long id);

    @Deprecated(forRemoval = true)
    List<PersonaResponse> getAllPersonas();

    @Deprecated(forRemoval = true)
    List<PersonaResponse> getPersonasBySinnerId(Long sinnerId);
}
