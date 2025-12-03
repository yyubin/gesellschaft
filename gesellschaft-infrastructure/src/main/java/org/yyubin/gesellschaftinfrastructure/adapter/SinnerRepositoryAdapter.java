package org.yyubin.gesellschaftinfrastructure.adapter;

import model.Sinner;
import org.springframework.stereotype.Repository;
import org.yyubin.gesellschaftinfrastructure.mapper.SinnerMapper;
import org.yyubin.gesellschaftinfrastructure.repository.SinnerJpaRepository;
import port.SinnerRepository;

import java.util.Optional;

@Repository
public class SinnerRepositoryAdapter implements SinnerRepository {

    private final SinnerJpaRepository jpaRepository;
    private final SinnerMapper mapper;

    public SinnerRepositoryAdapter(SinnerJpaRepository jpaRepository, SinnerMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Sinner> findById(Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);  // JPA → Domain 변환
    }

    @Override
    public Sinner save(Sinner sinner) {
        var jpa = mapper.toJpa(sinner);  // Domain → JPA 변환
        var saved = jpaRepository.save(jpa);
        return mapper.toDomain(saved);  // JPA → Domain 변환
    }
}
