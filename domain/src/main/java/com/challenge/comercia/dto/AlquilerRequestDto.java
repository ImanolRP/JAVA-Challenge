package com.challenge.comercia.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The type Alquiler request dto.
 */
@Getter
@Setter
@NoArgsConstructor
public class AlquilerRequestDto {

  @NotNull(message = "El ID del cliente es obligatorio")
  private Long clienteId;

  @NotEmpty(message = "Debe indicarse al menos un ID de coche")
  @Size(min = 1, message = "Debe indicarse al menos un ID de coche")
  private List<Long> cocheIds;

  @NotNull(message = "La fecha de inicio es obligatoria")
  private LocalDate fechaInicio;

  @NotNull(message = "La fecha de fin es obligatoria")
  private LocalDate fechaFin;

}

