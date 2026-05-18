package com.challenge.comercia.repository.custom;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.challenge.comercia.entity.Coche;

/**
 * The interface Coche repository custom.
 */
public interface CocheRepositoryCustom {

  /**
   * Busca coches disponibles en un rango y opcionalmente por tipo.
   *
   * @param fechaInicio fecha de inicio del rango
   * @param fechaFin fecha de fin del rango
   * @param cocheTipoId tipo de coche opcional
   * @param pageable the pageable
   * @return coches disponibles
   */
  Page<Coche> findDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
      String cocheTipoId, Pageable pageable);

}

