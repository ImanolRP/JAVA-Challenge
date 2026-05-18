package com.challenge.comercia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Coche.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COCHE")
public class Coche {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COCHE")
  @SequenceGenerator(name = "SEQ_COCHE", sequenceName = "SEQ_COCHE",
      allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @Column(name = "MATRICULA")
  private String matricula;

  @ManyToOne
  @JoinColumn(name = "COCHE_TIPO_ID", insertable = false, updatable = false)
  private CocheTipo cocheTipo;

}

