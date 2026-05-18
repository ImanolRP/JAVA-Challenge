package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

/**
 * The type Premium precio alquiler calculator.
 *
 * PREMIUM : dias * 300
 *
 */
@Component
public class PremiumPrecioAlquilerCalculator
    implements PrecioAlquilerCalculator {

  private static final String TIPO = "PREMIUM";

  @Override
  public String getTipo() {
    return TIPO;
  }

  @Override
  public BigDecimal calcular(BigDecimal precioBase, int dias) {
    return precioBase.multiply(BigDecimal.valueOf(dias));
  }

}

