package com.challenge.comercia.dto;

import lombok.*;

/**
 * The type Coche disponible dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocheDisponibleDto {

  private Long id;
  private String matricula;
  private String cocheTipoNombre;

}

