package service;

import dto.PersonaResponse;

import java.util.List;

public interface PersonaService {

    PersonaResponse getPersonaById(Long id);
    List<PersonaResponse> getAllPersonas();
    List<PersonaResponse> getPersonasBySinnerId(Long sinnerId);

    // Cursor-based pagination
    List<PersonaResponse> getPersonasWithCursor(Long afterId, Long beforeId, Integer limit);
    long countPersonas();
}
