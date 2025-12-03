package service.impl;

import dto.PersonaResponse;
import dto.SinnerResponse;
import exception.PersonaNotFoundException;
import lombok.RequiredArgsConstructor;
import model.Sinner;
import model.persona.Persona;
import port.PersonaRepository;
import port.SinnerRepository;
import service.PersonaService;

import java.util.List;

@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final SinnerRepository sinnerRepository;

    @Override
    public PersonaResponse getPersonaById(Long id) {
        Persona persona = personaRepository.findById(id)
            .orElseThrow(() -> new PersonaNotFoundException(id));

        // Persona는 Sinner에 대한 참조를 직접 가지지 않으므로,
        // JPA 엔티티를 통해 조회하거나 별도로 조회해야 함
        // 임시로 null 처리 (실제로는 PersonaJpa에서 Sinner 정보를 가져와야 함)
        return PersonaResponse.from(persona, null);
    }

    @Override
    public List<PersonaResponse> getAllPersonas() {
        List<Persona> personas = personaRepository.findAll();
        return personas.stream()
            .map(persona -> PersonaResponse.from(persona, null))
            .toList();
    }

    @Override
    public List<PersonaResponse> getPersonasBySinnerId(Long sinnerId) {
        List<Persona> personas = personaRepository.findBySinnerId(sinnerId);

        // Sinner 정보 조회
        Sinner sinner = sinnerRepository.findById(sinnerId)
            .orElse(null);
        SinnerResponse sinnerResponse = sinner != null ? SinnerResponse.from(sinner) : null;

        return personas.stream()
            .map(persona -> PersonaResponse.from(persona, sinnerResponse))
            .toList();
    }
}
