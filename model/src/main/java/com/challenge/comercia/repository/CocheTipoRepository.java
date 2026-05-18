package com.challenge.comercia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.comercia.entity.CocheTipo;

/**
 * The interface Coche tipo repository.
 */
@Repository
public interface CocheTipoRepository extends JpaRepository<CocheTipo, String> {
  // empty interface
}

