package port;

import model.persona.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository {

    Optional<Persona> findById(Long id);
    List<Persona> findAll();
    List<Persona> findBySinnerId(Long sinnerId);

    // Cursor-based pagination
    List<Persona> findAllWithCursor(Long afterId, Long beforeId, Integer limit);
    long count();

}
