package org.yyubin.gesellschaftinfrastructure.adapter;

import lombok.RequiredArgsConstructor;
import model.Sinner;
import org.springframework.stereotype.Repository;
import org.yyubin.gesellschaftinfrastructure.mapper.SinnerMapper;
import org.yyubin.gesellschaftinfrastructure.repository.SinnerJpaRepository;
import port.SinnerRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SinnerRepositoryAdapter implements SinnerRepository {

    private final SinnerJpaRepository jpaRepository;
    private final SinnerMapper mapper;

    @Override
    public Optional<Sinner> findById(Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Sinner save(Sinner sinner) {
        var jpa = mapper.toJpa(sinner);
        var saved = jpaRepository.save(jpa);
        return mapper.toDomain(saved);
    }
}
