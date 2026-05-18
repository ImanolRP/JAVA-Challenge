package com.challenge.comercia.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The type Devolucion alquiler request dto.
 */
@Getter
@Setter
@NoArgsConstructor
public class DevolucionAlquilerRequestDto {

  @NotNull(message = "El id del alquiler es obligatorio")
  private Long alquilerId;

  @NotNull(message = "La fecha de devolucion es obligatoria")
  private LocalDate fechaDevolucion;

}

