package com.challenge.comercia.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Alquiler coche.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ALQUILER_COCHE")
public class AlquilerCoche {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,
      generator = "SEQ_ALQUILER_COCHE")
  @SequenceGenerator(name = "SEQ_ALQUILER_COCHE",
      sequenceName = "SEQ_ALQUILER_COCHE", allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "ALQUILER_ID", insertable = false, updatable = false)
  private Alquiler alquiler;

  @ManyToOne
  @JoinColumn(name = "COCHE_ID", insertable = false, updatable = false)
  private Coche coche;

  @Column(name = "COCHE_TIPO_SNAPSHOT")
  private String cocheTipoSnapshot;

  @Column(name = "DIAS_BASE")
  private Integer diasBase;

  @Column(name = "PRECIO_BASE")
  private BigDecimal precioBase;

  @Column(name = "DIAS_EXTRA")
  private Integer diasExtra;

  @Column(name = "PRECIO_EXTRA")
  private BigDecimal precioExtra;

  @Column(name = "TOTAL_COCHE")
  private BigDecimal totalCoche;

  @Column(name = "FECHA_DEVOLUCION")
  private LocalDate fechaDevolucion;

}

