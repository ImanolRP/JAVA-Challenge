package com.challenge.comercia.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Coche tipo.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COCHE_TIPO")
public class CocheTipo {

  @Id
  @Column(name = "ID")
  private String id;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "PRECIO_BASE")
  private BigDecimal precioBase;

  @Column(name = "PUNTOS_LEALTAD")
  private Integer puntosLealtad;

}

