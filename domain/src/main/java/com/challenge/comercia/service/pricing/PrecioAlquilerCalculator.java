package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;

/**
 * The interface Precio alquiler calculator.
 */
public interface PrecioAlquilerCalculator {

  /**
   * tipo de coche soportado (por ejemplo: PREMIUM, SUV, SMALL).
   */
  String getTipo();

  /**
   * Calcular big decimal el precio de alquiler para el tipo soportado.
   *
   * @param precioBase precio base diario del tipo
   * @param dias numero de dias de alquiler
   * @return precio total del alquiler
   */
  BigDecimal calcular(BigDecimal precioBase, int dias);

}

