package com.challenge.comercia.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.challenge.comercia.dto.AlquilerRequestDto;
import com.challenge.comercia.dto.AlquilerResponseDto;
import com.challenge.comercia.dto.DevolucionAlquilerRequestDto;
import com.challenge.comercia.dto.DevolucionAlquilerResponseDto;
import com.challenge.comercia.service.AlquilerService;

import jakarta.validation.Valid;


/**
 * The type Alquiler controller.
 */
@RestController
@RequestMapping("/alquiler")
public class AlquilerController {

  private static final Logger LOG =
      LoggerFactory.getLogger(AlquilerController.class);

  @Autowired
  private AlquilerService alquilerService;

  /**
   * Endpoint para crear un nuevo alquiler de coches para un cliente. Valida
   * forma de los datos.
   *
   * @param request AlquilerRequestDto con los datos del alquiler a crear
   * @return 201 Created con AlquilerResponseDto o 400/422 si hay errores de
   *         validacion
   */
  @PostMapping()
  public @ResponseBody ResponseEntity<AlquilerResponseDto> crearAlquiler(
      @Valid @RequestBody AlquilerRequestDto request) {
    LOG.info("POST /alquiler - clienteId={}, coches={}, inicio={}, fin={}",
        request.getClienteId(), request.getCocheIds(), request.getFechaInicio(),
        request.getFechaFin());

    AlquilerResponseDto response = alquilerService.crearAlquiler(request);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Endpoint para registrar la devolucion de coches de un alquiler.
   *
   * Para poder abordar el MVP, se asume que la devolucion se realiza en una
   * sola operacion, es decir, no se permiten devoluciones parciales. En caso de
   * querer permitir devoluciones parciales, se deberia modificar el request
   * para incluir los coches que se devuelven y el servicio para actualizar el
   * estado del alquiler en consecuencia.
   *
   * @param request DevolucionAlquilerRequestDto con los datos del alquiler y la
   *        fecha de devolucion
   * @return estado actualizado del alquiler tras la devolucion o 400/422 si hay
   *         errores de validacion
   */
  @PostMapping("/devolucion")
  public @ResponseBody ResponseEntity<DevolucionAlquilerResponseDto> devolverCoches(
      @Valid @RequestBody DevolucionAlquilerRequestDto request) {
    LOG.info("POST /alquiler/devolucion - alquilerId={}, fecha={}",
        request.getAlquilerId(), request.getFechaDevolucion());

    DevolucionAlquilerResponseDto response =
        alquilerService.devolverCoches(request);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}

