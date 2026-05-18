package com.challenge.comercia.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Alquiler.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ALQUILER")
public class Alquiler {

  @Id
  @GeneratedValue(generator = "SEQ_ALQUILER",
      strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = "SEQ_ALQUILER", sequenceName = "SEQ_ALQUILER",
      allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "CLIENTE_ID", insertable = false, updatable = false)
  private Cliente cliente;

  @Column(name = "FECHA_INICIO")
  private LocalDate fechaInicio;

  @Column(name = "FECHA_FIN")
  private LocalDate fechaFin;

  @Column(name = "TOTAL_BASE")
  private BigDecimal totalBase;

  @Column(name = "TOTAL_RECARGO")
  private BigDecimal totalRecargo;

  @Column(name = "TOTAL_ALQUILER")
  private BigDecimal totalAlquiler;

  @Column(name = "PUNTOS_LEALTAD")
  private Integer puntosLealtad;

}

