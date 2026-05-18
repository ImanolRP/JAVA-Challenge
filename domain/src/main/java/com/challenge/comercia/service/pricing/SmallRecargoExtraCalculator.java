package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;


/**
 * The type Small recargo extra calculator.
 */
@Component
public class SmallRecargoExtraCalculator implements RecargoExtraCalculator {

  private static final String TIPO = "SMALL";

  @Override
  public String getTipo() {
    return TIPO;
  }

  @Override
  public BigDecimal calcular(RecargoContext context, int diasExtra) {
    BigDecimal precioExtraDia = context.getPrecioBase(getTipo())
        .multiply(new BigDecimal("1.30")).setScale(2, RoundingMode.HALF_UP);
    return precioExtraDia.multiply(BigDecimal.valueOf(diasExtra));
  }

}

