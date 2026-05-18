package com.challenge.comercia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.comercia.entity.Cliente;

/**
 * The interface Cliente repository.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  // empty interface
}

