package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;


/**
 * The type Premium recargo extra calculator.
 */
@Component
public class PremiumRecargoExtraCalculator implements RecargoExtraCalculator {

  private static final String TIPO = "PREMIUM";

  @Override
  public String getTipo() {
    return TIPO;
  }

  @Override
  public BigDecimal calcular(RecargoContext context, int diasExtra) {
    BigDecimal precioExtraDia = context.getPrecioBase(getTipo())
        .multiply(new BigDecimal("1.20")).setScale(2, RoundingMode.HALF_UP);
    return precioExtraDia.multiply(BigDecimal.valueOf(diasExtra));
  }

}

