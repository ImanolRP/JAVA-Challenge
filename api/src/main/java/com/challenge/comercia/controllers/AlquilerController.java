package com.challenge.comercia.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.challenge.comercia.dto.AlquilerRequestDto;
import com.challenge.comercia.dto.AlquilerResponseDto;
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

}

