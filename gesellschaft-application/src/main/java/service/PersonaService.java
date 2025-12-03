package service;

import dto.PersonaResponse;

import java.util.List;

public interface PersonaService {

    PersonaResponse getPersonaById(Long id);
    List<PersonaResponse> getAllPersonas();
    List<PersonaResponse> getPersonasBySinnerId(Long sinnerId);
}
