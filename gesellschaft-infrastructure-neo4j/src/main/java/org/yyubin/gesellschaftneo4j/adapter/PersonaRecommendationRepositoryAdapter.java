package org.yyubin.gesellschaftneo4j.adapter;

import lombok.RequiredArgsConstructor;
import model.persona.Persona;
import org.springframework.stereotype.Repository;
import org.yyubin.gesellschaftneo4j.mapper.PersonaNeo4jMapper;
import org.yyubin.gesellschaftneo4j.repository.PersonaNeo4jRepository;
import port.PersonaRecommendationRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonaRecommendationRepositoryAdapter implements PersonaRecommendationRepository {

    private final PersonaNeo4jRepository neo4jRepository;
    private final PersonaNeo4jMapper mapper;

    @Override
    public List<Persona> findSimilarPersonas(Long personaId, int limit) {
        var nodes = neo4jRepository.findSimilarPersonas(personaId, limit);
        return mapper.toDomainList(nodes);
    }
}
