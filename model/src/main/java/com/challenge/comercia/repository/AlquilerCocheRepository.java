package com.challenge.comercia.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.challenge.comercia.entity.AlquilerCoche;

/**
 * The interface Alquiler coche repository.
 */
@Repository
public interface AlquilerCocheRepository
    extends JpaRepository<AlquilerCoche, Long> {

  /**
   * Obtiene todas las lineas de coche de un alquiler.
   *
   * @param alquilerId id del alquiler
   * @return lineas de alquiler
   */
  List<AlquilerCoche> findByAlquilerId(Long alquilerId);

  /**
   * Devuelve los registros de AlquilerCoche cuyos coches estan ocupados en el
   * rango de fechas dado (solapamiento de periodos).
   *
   * @param cocheIds lista de IDs de coche a comprobar
   * @param fechaInicio fecha inicio del rango solicitado
   * @param fechaFin fecha fin del rango solicitado
   * @return lista de AlquilerCoche que solapan con el rango
   */
  @Query("SELECT ac " //
      + " FROM AlquilerCoche ac " //
      + " WHERE ac.cocheId IN :cocheIds " //
      + "   AND ac.alquiler.fechaInicio < :fechaFin " //
      + "   AND ac.alquiler.fechaFin > :fechaInicio")
  List<AlquilerCoche> findCochesOcupados(@Param("cocheIds") List<Long> cocheIds,
      @Param("fechaInicio") LocalDate fechaInicio,
      @Param("fechaFin") LocalDate fechaFin);

}

