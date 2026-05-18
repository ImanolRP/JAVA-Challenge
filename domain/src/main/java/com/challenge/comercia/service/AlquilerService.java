package com.challenge.comercia.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.challenge.comercia.dto.*;
import com.challenge.comercia.entity.*;
import com.challenge.comercia.repository.*;
import com.challenge.comercia.service.pricing.PrecioAlquilerCalculatorResolver;
import com.challenge.comercia.service.pricing.RecargoContext;
import com.challenge.comercia.service.pricing.RecargoExtraCalculatorResolver;


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
  private CocheTipoRepository cocheTipoRepository;

  @Autowired
  private AlquilerRepository alquilerRepository;

  @Autowired
  private AlquilerCocheRepository alquilerCocheRepository;

  @Autowired
  private PrecioAlquilerCalculatorResolver precioCalculatorResolver;

  @Autowired
  private RecargoExtraCalculatorResolver recargoExtraCalculatorResolver;

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
      BigDecimal precioCoche = precioCalculatorResolver.resolve(tipo)
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

  /**
   * Busca coches disponibles en un rango de fechas y opcionalmente por tipo.
   *
   * @param request datos de filtro de disponibilidad
   * @param pageable
   * @return listado de coches disponibles
   */
  public Page<CocheDisponibleDto> buscarCochesDisponibles(
      DisponibilidadCochesRequestDto request, Pageable pageable) {
    // 1. Validaciones de negocio
    this.validarBuscarDisponibilidad(request);

    // 2. Buscar coches disponibles en base de datos
    Page<Coche> cochesPage =
        cocheRepository.findDisponibles(request.getFechaInicio(),
            request.getFechaFin(), request.getCocheTipoId(), pageable);

    // 3. Mapear a DTO Page de respuesta
    List<CocheDisponibleDto> content = cochesPage.getContent().stream()
        .map(coche -> CocheDisponibleDto.builder() //
            .id(coche.getId()) //
            .matricula(coche.getMatricula()) //
            .cocheTipoNombre(coche.getCocheTipo().getNombre()).build()) //
        .toList();

    return new PageImpl<>(content, pageable, cochesPage.getTotalElements());
  }

  private void validarBuscarDisponibilidad(
      DisponibilidadCochesRequestDto request) {
    StringBuilder sb = new StringBuilder();

    if (request.getFechaFin().isBefore(request.getFechaInicio())) {
      sb.append("La fecha fin no puede ser anterior a la fecha inicio. ");
    }

    if (StringUtils.isNotBlank(request.getCocheTipoId()) && BooleanUtils
        .isNotTrue(cocheTipoRepository.existsById(request.getCocheTipoId()))) {
      sb.append("Tipo de coche no encontrado: ") //
          .append(request.getCocheTipoId()) //
          .append(". ");
    }

    if (BooleanUtils.isNotTrue(sb.isEmpty())) {
      throw new IllegalArgumentException(sb.toString());
    }
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

  /**
   * Registra registrar la devolucion de coches de un alquiler.
   *
   * <p>
   * Valida coherencia de campos, que los coches pertenezcan al alquiler y
   * actualiza el estado del alquiler calculando recargos por devolucion tardia
   * segun el tipo de coche y el numero de dias de retraso.
   * </p>
   *
   * @param request DTO con los datos de devolucion
   * @return DTO de resumen actualizado del alquiler
   * @throws IllegalArgumentException si alguna validacion falla
   */
  @Transactional
  public DevolucionAlquilerResponseDto devolverCoches(
      DevolucionAlquilerRequestDto request) {
    // 1. Validaciones de negocio
    this.validarDevolverCoches(request);

    // 2. Obtener el alquiler y los alquileres de coche
    Alquiler alquiler =
        alquilerRepository.findById(request.getAlquilerId()).orElse(null);
    List<AlquilerCoche> alquileresCoches =
        alquilerCocheRepository.findByAlquilerId(alquiler.getId());
    Map<String, BigDecimal> preciosBaseByTipo = obtenerPreciosBaseByTipo();
    RecargoContext recargoContext = new RecargoContext(preciosBaseByTipo);

    // 3. Calcular recargos por cada coche y actualizar de alquileres de coche
    for (AlquilerCoche alquilerCoche : alquileresCoches) {
      int diasExtra = calcularDiasExtra(alquiler.getFechaFin(),
          request.getFechaDevolucion());
      BigDecimal precioExtra = recargoExtraCalculatorResolver //
          .resolve(alquilerCoche.getCocheTipoSnapshot()) //
          .calcular(recargoContext, diasExtra);

      alquilerCoche.setFechaDevolucion(request.getFechaDevolucion());
      alquilerCoche.setDiasExtra(diasExtra);
      alquilerCoche.setPrecioExtra(precioExtra);
      alquilerCoche
          .setTotalCoche(alquilerCoche.getPrecioBase().add(precioExtra));
      alquilerCocheRepository.save(alquilerCoche);
    }

    // 4. Calcular totales del alquiler
    BigDecimal totalRecargoRegistrado = BigDecimal.ZERO;
    BigDecimal totalFactura = BigDecimal.ZERO;
    List<DevolucionCocheResumenDto> detalle = new ArrayList<>();
    for (AlquilerCoche alquilerCoche : alquileresCoches) {
      totalRecargoRegistrado =
          totalRecargoRegistrado.add(alquilerCoche.getPrecioExtra());
      BigDecimal recargo = alquilerCoche.getPrecioExtra();
      int diasExtra = alquilerCoche.getDiasExtra();
      LocalDate fechaUsadaCalculo = alquilerCoche.getFechaDevolucion();
      BigDecimal totalCoche = alquilerCoche.getPrecioBase().add(recargo);
      totalFactura = totalFactura.add(totalCoche);

      detalle.add(DevolucionCocheResumenDto.builder() //
          .cocheId(alquilerCoche.getCocheId()) //
          .cocheTipo(alquilerCoche.getCocheTipoSnapshot()) //
          .fechaDevolucion(fechaUsadaCalculo) //
          .diasExtra(diasExtra) //
          .precioExtra(recargo) //
          .totalCoche(totalCoche) //
          .build());
    }

    // 5. Actualizar valores calculados del alquiler
    alquiler.setTotalRecargo(totalRecargoRegistrado);
    alquiler
        .setTotalAlquiler(alquiler.getTotalBase().add(totalRecargoRegistrado));
    alquilerRepository.save(alquiler);

    LOG.info(
        "Alquiler con ID {} - Devolucion registrada con fecha {} - Recargo total registrado: {} EUR - Total factura: {} EUR",
        alquiler.getId(), request.getFechaDevolucion(), totalRecargoRegistrado,
        totalFactura);

    return DevolucionAlquilerResponseDto.builder() //
        .alquilerId(alquiler.getId()) //
        .fechaInicio(alquiler.getFechaInicio()) //
        .fechaFin(alquiler.getFechaFin()) //
        .fechaDevolucion(request.getFechaDevolucion()) //
        .totalBase(alquiler.getTotalBase()) //
        .totalRecargoRegistrado(totalRecargoRegistrado) //
        .totalFacturaRegistrada(alquiler.getTotalAlquiler()) //
        .detalleCoches(detalle) //
        .build();
  }

  private void validarDevolverCoches(DevolucionAlquilerRequestDto request) {
    StringBuilder sb = new StringBuilder();

    // Validar existencia del alquiler
    Alquiler alquiler =
        alquilerRepository.findById(request.getAlquilerId()).orElse(null);
    if (Objects.isNull(alquiler)) {
      sb.append("Alquiler no encontrado con ID: ") //
          .append(request.getAlquilerId()) //
          .append(". ");
    }

    // Validaciones de coherencia de fechas
    if (Objects.nonNull(alquiler)
        && request.getFechaDevolucion().isBefore(alquiler.getFechaInicio())) {
      sb.append(
          "La fecha de devolucion no puede ser anterior a la fecha de inicio del alquiler. ");
    }

    // No se puede devolver dos veces un mismo coche
    List<AlquilerCoche> alquileresCoche =
        alquilerCocheRepository.findByAlquilerId(request.getAlquilerId());
    List<Long> cochesYaDevueltos = alquileresCoche.stream() //
        .filter(ac -> Objects.nonNull(ac.getFechaDevolucion())) //
        .map(AlquilerCoche::getCocheId) //
        .toList();
    if (BooleanUtils.isNotTrue(CollectionUtils.isEmpty(cochesYaDevueltos))) {
      sb.append("Los siguientes coches ya han sido devueltos: ") //
          .append(cochesYaDevueltos) //
          .append(". ");
    }

    if (BooleanUtils.isNotTrue(sb.isEmpty())) {
      throw new IllegalArgumentException(sb.toString());
    }

  }

  private int calcularDiasExtra(LocalDate fechaFinAlquiler,
      LocalDate fechaDevolucion) {
    long dias = ChronoUnit.DAYS.between(fechaFinAlquiler, fechaDevolucion);
    // Si la devolucion es antes o en la fecha fin, no hay dias extra
    return (int) Math.max(0, dias);
  }

  private Map<String, BigDecimal> obtenerPreciosBaseByTipo() {
    List<CocheTipo> tipos = cocheTipoRepository.findAll();

    return tipos.stream()
        .collect(Collectors.toMap(CocheTipo::getId, CocheTipo::getPrecioBase));
  }

}

