package com.challenge.comercia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.comercia.entity.Alquiler;

/**
 * The interface Alquiler repository.
 */
@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
  // empty interface
}

