package com.challenge.comercia.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.challenge.comercia.dto.AlquilerCocheDetalleDto;
import com.challenge.comercia.dto.AlquilerRequestDto;
import com.challenge.comercia.dto.AlquilerResponseDto;
import com.challenge.comercia.entity.Alquiler;
import com.challenge.comercia.entity.AlquilerCoche;
import com.challenge.comercia.entity.Cliente;
import com.challenge.comercia.entity.Coche;
import com.challenge.comercia.repository.AlquilerCocheRepository;
import com.challenge.comercia.repository.AlquilerRepository;
import com.challenge.comercia.repository.ClienteRepository;
import com.challenge.comercia.repository.CocheRepository;
import com.challenge.comercia.service.pricing.PrecioAlquilerCalculatorResolver;


/**
 * The type Alquiler service.
 */
@Service
@Transactional(readOnly = true)
public class AlquilerService {

  private static final Logger LOG =
      LoggerFactory.getLogger(AlquilerService.class);

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private CocheRepository cocheRepository;

  @Autowired
  private AlquilerRepository alquilerRepository;

  @Autowired
  private AlquilerCocheRepository alquilerCocheRepository;

  @Autowired
  private PrecioAlquilerCalculatorResolver precioCalculatorResolver;

  /**
   * Crea un nuevo alquiler para un cliente con uno o varios coches.
   *
   * <p>
   * Valida coherencia de campos, disponibilidad de coches y persiste el
   * contrato calculando el precio por tipo de coche y los puntos de fidelidad.
   * </p>
   *
   * @param request DTO con los datos del alquiler
   * @return DTO de respuesta con el detalle del alquiler creado
   * @throws IllegalArgumentException si alguna validacion falla
   */
  @Transactional()
  public AlquilerResponseDto crearAlquiler(AlquilerRequestDto request) {
    // 1. Validaciones de negcio
    this.validarCrearAlquiler(request);

    // 2. Persistir alquiler con datos minimos
    Alquiler alquiler = new Alquiler();
    alquiler.setClienteId(request.getClienteId());
    alquiler.setFechaInicio(request.getFechaInicio());
    alquiler.setFechaFin(request.getFechaFin());
    alquiler.setTotalBase(BigDecimal.ZERO);
    alquiler.setTotalRecargo(BigDecimal.ZERO);
    alquiler.setTotalAlquiler(BigDecimal.ZERO);
    alquiler.setPuntosLealtad(NumberUtils.INTEGER_ZERO);
    alquiler = alquilerRepository.save(alquiler);

    // 3. Calcular y persistir lineas de alquiler de coche
    List<AlquilerCocheDetalleDto> detalles = new ArrayList<>();
    BigDecimal totalBase = BigDecimal.ZERO;
    int totalPuntos = NumberUtils.INTEGER_ZERO;
    long diasBase = ChronoUnit.DAYS.between(request.getFechaInicio(),
        request.getFechaFin());
    // Obtener datos de los coches de la base de datos
    List<Coche> coches = cocheRepository.findAllById(request.getCocheIds());
    final Long alquilerId = alquiler.getId();
    for (Coche coche : coches) {
      String tipo = coche.getCocheTipo().getId();
      BigDecimal precioBaseDia = coche.getCocheTipo().getPrecioBase();
      int puntosCoche = coche.getCocheTipo().getPuntosLealtad();
      // calcular precio total del alquiler para el coche segun su tipo y numero
      // de dias
      BigDecimal precioCoche =
          precioCalculatorResolver.resolve(tipo)
              .calcular(precioBaseDia, (int) diasBase);

      // Agregar detalle para respuesta
      detalles.add(AlquilerCocheDetalleDto.builder() //
          .cocheId(coche.getId()) //
          .matricula(coche.getMatricula()) //
          .cocheTipo(tipo) //
          .diasBase((int) diasBase) //
          .precioBase(precioBaseDia) //
          .totalCoche(precioCoche) //
          .puntosLealtad(puntosCoche) //
          .build() //
      );

      // Persistr linea de alquiler de coche
      AlquilerCoche ac = new AlquilerCoche();
      ac.setAlquilerId(alquilerId);
      ac.setCocheId(coche.getId());
      ac.setCocheTipoSnapshot(tipo);
      ac.setDiasBase((int) diasBase);
      ac.setPrecioBase(precioCoche);
      ac.setDiasExtra(NumberUtils.INTEGER_ZERO);
      ac.setPrecioExtra(BigDecimal.ZERO);
      ac.setTotalCoche(precioCoche);
      alquilerCocheRepository.save(ac);

      // acumular totales para el alquiler
      totalBase = totalBase.add(precioCoche);
      totalPuntos += puntosCoche;
    }

    // 4. Actualizar valores calculados del alquiler
    alquiler.setTotalBase(totalBase);
    alquiler.setTotalAlquiler(totalBase);
    alquiler.setPuntosLealtad(totalPuntos);
    alquilerRepository.save(alquiler);

    // 5. Actualizar puntos de fidelidad del cliente
    Cliente cliente =
        clienteRepository.findById(request.getClienteId()).orElseThrow();
    cliente.setTotalPuntosLealtad((cliente.getTotalPuntosLealtad() == null ? 0L
        : cliente.getTotalPuntosLealtad()) + totalPuntos);
    clienteRepository.save(cliente);

    LOG.info(
        "Alquiler creado con ID {} para cliente {} - Total: {} EUR - Puntos: {}",
        alquilerId, cliente.getId(), totalBase, totalPuntos);

    return AlquilerResponseDto.builder() //
        .alquilerId(alquilerId) //
        .clienteId(cliente.getId()) //
        .clienteNombre(cliente.getNombreCompleto()) //
        .fechaInicio(request.getFechaInicio()) //
        .fechaFin(request.getFechaFin()) //
        .coches(detalles) //
        .totalBase(totalBase) //
        .totalAlquiler(totalBase) //
        .puntosLealtadGenerados(totalPuntos) //
        .totalPuntosLealtadCliente(cliente.getTotalPuntosLealtad()) //
        .build();
  }

  private void validarCrearAlquiler(AlquilerRequestDto request) {
    StringBuilder sb = new StringBuilder();

    // Validaciones de coherencia de fechas
    LocalDate fechaInicio = request.getFechaInicio();
    LocalDate fechaFin = request.getFechaFin();

    if (BooleanUtils.isNotTrue(fechaFin.isAfter(fechaInicio))) {
      sb.append("La fecha fin debe ser posterior a la fecha de inicio. ");
    }

    long diasBase = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    if (diasBase <= NumberUtils.INTEGER_ZERO) {
      sb.append("El numero de dias de alquiler debe ser mayor que cero. ");
    }

    // Validar existencia del cliente
    Cliente cliente =
        clienteRepository.findById(request.getClienteId()).orElse(null);
    if (Objects.isNull(cliente)) {
      sb.append("Cliente no encontrado con ID: ") //
          .append(request.getClienteId()) //
          .append(". ");
    }

    // Validar existencia de todos los coches
    List<Long> cocheIds = request.getCocheIds();
    List<Coche> coches = cocheRepository.findAllById(cocheIds);

    if (coches.size() != cocheIds.size()) {
      List<Long> encontrados = coches.stream() //
          .map(Coche::getId) //
          .collect(Collectors.toList());
      List<Long> noEncontrados = cocheIds.stream() //
          .filter(id -> BooleanUtils.isNotTrue(encontrados.contains(id))) //
          .collect(Collectors.toList());
      sb.append("Coches no encontrados con IDs: ") //
          .append(noEncontrados) //
          .append(". ");
    }

    // Validar disponibilidad de los coches
    List<AlquilerCoche> cochesOcupados = alquilerCocheRepository
        .findCochesOcupados(cocheIds, fechaInicio, fechaFin);

    if (BooleanUtils.isNotTrue(CollectionUtils.isEmpty(cochesOcupados))) {
      List<Long> idsOcupados =
          cochesOcupados.stream().map(AlquilerCoche::getCocheId).distinct()
              .collect(Collectors.toList());
      sb.append(
          "Los siguientes coches no estan disponibles para el periodo solicitado: ") //
          .append(idsOcupados) //
          .append(". ");
    }

    if (BooleanUtils.isNotTrue(sb.isEmpty())) {
      throw new IllegalArgumentException(sb.toString());
    }

  }

}

