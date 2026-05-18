package com.challenge.comercia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CLIENTE")
public class Cliente {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CLIENTE")
  @SequenceGenerator(name = "SEQ_CLIENTE", sequenceName = "SEQ_CLIENTE",
      allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @Column(name = "NIF")
  private String nif;

  @Column(name = "NOMBRE_COMPLETO")
  private String nombreCompleto;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "TOTAL_PUNTOS_LEALTAD")
  private Long totalPuntosLealtad;

}

