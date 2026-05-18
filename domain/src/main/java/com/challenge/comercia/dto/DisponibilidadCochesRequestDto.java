package com.challenge.comercia.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Disponibilidad coches request dto.
 */
@Getter
@Setter
@NoArgsConstructor
public class DisponibilidadCochesRequestDto {

  @NotNull(message = "La fecha de inicio es obligatoria")
  private LocalDate fechaInicio;

  @NotNull(message = "La fecha de fin es obligatoria")
  private LocalDate fechaFin;

  private String cocheTipoId;

}

