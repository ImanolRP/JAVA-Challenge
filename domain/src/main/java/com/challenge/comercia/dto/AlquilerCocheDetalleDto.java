package com.challenge.comercia.dto;

import java.math.BigDecimal;

import lombok.*;

/**
 * The type Alquiler coche detalle dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerCocheDetalleDto {

  private Long cocheId;
  private String matricula;
  private String cocheTipo;
  private Integer diasBase;
  private BigDecimal precioBase;
  private BigDecimal totalCoche;
  private Integer puntosLealtad;

}

