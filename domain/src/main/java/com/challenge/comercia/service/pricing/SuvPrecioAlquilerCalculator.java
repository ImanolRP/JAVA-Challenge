package com.challenge.comercia.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/**
 * The type Suv precio alquiler calculator.
 *
 * SUV : dias 1-7 -> 150/d | dias 8-30 -> 120/d | dias +31 -> 75/d
 *
 */
@Component
public class SuvPrecioAlquilerCalculator implements PrecioAlquilerCalculator {

  private static final String TIPO = "SUV";

  @Override
  public String getTipo() {
    return TIPO;
  }

  @Override
  public BigDecimal calcular(BigDecimal precioBase, int dias) {
    // calculamos minimo entre dias y 7
    int tramo1 = Math.min(dias, 7);
    // calculamos minimo entre dias y 30, a continuacion restamos los 7 del
    // tramo anterior, max con 0 para evitar negativos
    int tramo2 = Math.max(0, Math.min(dias, 30) - 7);
    // calculamos el maximo entre 0 y dias - 30 para obtener los dias del
    // tramo 3, evitando negativos
    int tramo3 = Math.max(0, dias - 30);

    // como hemos calculado los tramos, ahora multiplicamos cada tramo por
    // su precio diario. al protegernos de negativos no es necesario validar
    // >0 en cada tramo ya que estariamos multiplicando por 0 en vez de por
    // un numero negativo
    BigDecimal p1 = precioBase.multiply(BigDecimal.valueOf(tramo1));
    BigDecimal p2 = precioBase.multiply(new BigDecimal("0.80"))
        .multiply(BigDecimal.valueOf(tramo2)).setScale(2, RoundingMode.HALF_UP);
    BigDecimal p3 = precioBase.multiply(new BigDecimal("0.50"))
        .multiply(BigDecimal.valueOf(tramo3)).setScale(2, RoundingMode.HALF_UP);
    // sumamos los tramos para obtener el precio total
    return p1.add(p2).add(p3);
  }

}

