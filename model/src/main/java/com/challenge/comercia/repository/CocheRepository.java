package com.challenge.comercia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.comercia.entity.Coche;

/**
 * The interface Coche repository.
 */
@Repository
public interface CocheRepository
    extends JpaRepository<Coche, Long>, CocheRepositoryCustom {
  // empty interface
}

