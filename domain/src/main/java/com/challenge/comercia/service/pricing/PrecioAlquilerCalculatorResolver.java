package com.challenge.comercia.service.pricing;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


/**
 * The type Precio alquiler calculator resolver.
 */
@Component
public class PrecioAlquilerCalculatorResolver {

  private final Map<String, PrecioAlquilerCalculator> calculatorsByTipo;

  public PrecioAlquilerCalculatorResolver(
      List<PrecioAlquilerCalculator> calculators) {
    this.calculatorsByTipo = calculators.stream().collect(Collectors
        .toMap(PrecioAlquilerCalculator::getTipo, Function.identity()));
  }

  /**
   * Obtiene la calculadora adecuada para el tipo indicado.
   *
   * @param tipo tipo de coche
   * @return calculadora asociada al tipo
   */
  public PrecioAlquilerCalculator resolve(String tipo) {
    PrecioAlquilerCalculator calculator = calculatorsByTipo.get(tipo);
    if (Objects.isNull(calculator)) {
      throw new IllegalArgumentException(
          "No existe estrategia de precio para tipo: " + tipo);
    }
    return calculator;
  }

}

