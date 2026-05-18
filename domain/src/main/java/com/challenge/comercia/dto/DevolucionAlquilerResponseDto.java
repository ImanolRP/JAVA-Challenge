package com.challenge.comercia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.*;

/**
 * The type Devolucion alquiler response dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevolucionAlquilerResponseDto {

  private Long alquilerId;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;
  private LocalDate fechaDevolucion;

  private BigDecimal totalBase;
  private BigDecimal totalRecargoRegistrado;
  private BigDecimal totalFacturaRegistrada;

  private List<DevolucionCocheResumenDto> detalleCoches;

}

