package com.challenge.comercia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;

/**
 * The type Devolucion coche resumen dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevolucionCocheResumenDto {

  private Long cocheId;
  private String cocheTipo;
  private LocalDate fechaDevolucion;
  private Integer diasExtra;
  private BigDecimal precioExtra;
  private BigDecimal totalCoche;

}

