package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/**
 * The type Small precio alquiler calculator.
 *
 * SMALL : dias 1-7 -> 50/d | dias +8 -> 30/d
 *
 */
@Component
public class SmallPrecioAlquilerCalculator implements PrecioAlquilerCalculator {

  private static final String TIPO = "SMALL";

  @Override
  public String getTipo() {
    return TIPO;
  }

  @Override
  public BigDecimal calcular(BigDecimal precioBase, int dias) {
    // calculamos minimo entre dias y 7
    int tramo1 = Math.min(dias, 7);
    // calculamos el maximo entre 0 y dias - 7 para obtener los dias del
    // tramo evitando negativos
    int tramo2 = Math.max(0, dias - 7);

    // como hemos calculado los tramos, ahora multiplicamos cada tramo por
    // su precio diario. al protegernos de negativos no es necesario validar
    // >0 en cada tramo ya que estariamos multiplicando por 0 en vez de por
    // un numero negativo
    BigDecimal p1 = precioBase.multiply(BigDecimal.valueOf(tramo1));
    BigDecimal p2 = precioBase.multiply(new BigDecimal("0.60"))
        .multiply(BigDecimal.valueOf(tramo2)).setScale(2, RoundingMode.HALF_UP);
    // sumamos los tramos para obtener el precio total
    return p1.add(p2);
  }

}

