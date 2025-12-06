package org.yyubin.gesellschaftinfrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yyubin.gesellschaftinfrastructure.jpa.PersonaJpa;

import java.util.List;

public interface PersonaJpaRepository extends JpaRepository<PersonaJpa, Long> {

    @Query("SELECT p FROM PersonaJpa p WHERE p.sinnerId = :sinnerId")
    List<PersonaJpa> findBySinnerId(@Param("sinnerId") Long sinnerId);

    // Forward pagination: after cursor
    @Query("SELECT p FROM PersonaJpa p WHERE p.id > :afterId ORDER BY p.id ASC")
    List<PersonaJpa> findAllAfterCursor(@Param("afterId") Long afterId);

    // Backward pagination: before cursor
    @Query("SELECT p FROM PersonaJpa p WHERE p.id < :beforeId ORDER BY p.id DESC")
    List<PersonaJpa> findAllBeforeCursor(@Param("beforeId") Long beforeId);

    // All personas ordered by id
    @Query("SELECT p FROM PersonaJpa p ORDER BY p.id ASC")
    List<PersonaJpa> findAllOrderedById();
}
