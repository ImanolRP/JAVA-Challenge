package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * The type Recargo context.
 */
public class RecargoContext {

  private final Map<String, BigDecimal> preciosBaseByTipo;

  public RecargoContext(Map<String, BigDecimal> preciosBaseByTipo) {
    this.preciosBaseByTipo = preciosBaseByTipo;
  }

  /**
   * Obtiene el precio base por tipo de coche.
   *
   * @param tipo tipo de coche
   * @return precio base
   */
  public BigDecimal getPrecioBase(String tipo) {
    BigDecimal precio = preciosBaseByTipo.get(tipo);
    if (Objects.isNull(precio)) {
      throw new IllegalArgumentException(
          "No existe precio base para tipo: " + tipo);
    }
    return precio;
  }

}

