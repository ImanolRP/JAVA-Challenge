package com.challenge.comercia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.*;

/**
 * The type Alquiler response dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerResponseDto {

  private Long alquilerId;
  private Long clienteId;
  private String clienteNombre;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;
  private List<AlquilerCocheDetalleDto> coches;
  private BigDecimal totalBase;
  private BigDecimal totalAlquiler;
  private Integer puntosLealtadGenerados;
  private Long totalPuntosLealtadCliente;

}

