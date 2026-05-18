package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/**
 * The type Suv recargo extra calculator.
 */
@Component
public class SuvRecargoExtraCalculator implements RecargoExtraCalculator {

  private static final String TIPO = "SUV";

  @Override
  public String getTipo() {
    return TIPO;
  }

  @Override
  public BigDecimal calcular(RecargoContext context, int diasExtra) {
    BigDecimal precioExtraDia = context.getPrecioBase(getTipo())
        .add(context.getPrecioBase("SMALL").multiply(new BigDecimal("0.60")))
        .setScale(2, RoundingMode.HALF_UP);
    return precioExtraDia.multiply(BigDecimal.valueOf(diasExtra));
  }

}

