package com.challenge.comercia.service.pricing;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * The type Recargo extra calculator resolver.
 */
@Component
public class RecargoExtraCalculatorResolver {

  private final Map<String, RecargoExtraCalculator> calculatorsByTipo;

  public RecargoExtraCalculatorResolver(
      List<RecargoExtraCalculator> calculators) {
    this.calculatorsByTipo = calculators.stream().collect(
        Collectors.toMap(RecargoExtraCalculator::getTipo, Function.identity()));
  }

  /**
   * Obtiene la calculadora asociada al tipo indicado.
   *
   * @param tipo tipo de coche
   * @return calculadora de recargo
   */
  public RecargoExtraCalculator resolve(String tipo) {
    RecargoExtraCalculator calculator = calculatorsByTipo.get(tipo);
    if (Objects.isNull(calculator)) {
      throw new IllegalArgumentException(
          "No existe estrategia de recargo para tipo: " + tipo);
    }
    return calculator;
  }
}

