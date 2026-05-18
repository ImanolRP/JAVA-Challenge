package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;


/**
 * The interface Recargo extra calculator.
 */
public interface RecargoExtraCalculator {

  /**
   * tipo de coche soportado (por ejemplo: PREMIUM, SUV, SMALL).
   */
  String getTipo();

  /**
   * Calcular big decimal el recargo total para dias extra.
   *
   * @param context contexto con precios base por tipo
   * @param diasExtra dias fuera de plazo
   * @return recargo total
   */
  BigDecimal calcular(RecargoContext context, int diasExtra);

}

