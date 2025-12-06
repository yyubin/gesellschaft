package org.yyubin.gesellschaftinfrastructure.adapter;

import lombok.RequiredArgsConstructor;
import model.persona.Persona;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.yyubin.gesellschaftinfrastructure.jpa.PersonaJpa;
import org.yyubin.gesellschaftinfrastructure.mapper.PersonaMapper;
import org.yyubin.gesellschaftinfrastructure.repository.PersonaJpaRepository;
import port.PersonaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepository {

    private final PersonaJpaRepository jpaRepository;
    private final PersonaMapper mapper;

    @Override
    public Optional<Persona> findById(Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<Persona> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public List<Persona> findBySinnerId(Long sinnerId) {
        return mapper.toDomainList(jpaRepository.findBySinnerId(sinnerId));
    }

    @Override
    public List<Persona> findAllWithCursor(Long afterId, Long beforeId, Integer limit) {
        List<PersonaJpa> jpaList;

        if (afterId != null && beforeId != null) {
            throw new IllegalArgumentException("Cannot use both 'after' and 'before' cursors");
        }

        int pageSize = limit != null ? limit + 1 : 21; // +1 to check if there's more

        if (afterId != null) {
            jpaList = jpaRepository.findAllAfterCursor(afterId);
        } else if (beforeId != null) {
            jpaList = jpaRepository.findAllBeforeCursor(beforeId);
        } else {
            jpaList = jpaRepository.findAllOrderedById();
        }

        if (jpaList.size() > pageSize) {
            jpaList = jpaList.subList(0, pageSize);
        }

        return mapper.toDomainList(jpaList);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
