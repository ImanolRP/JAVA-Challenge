package com.challenge.comercia.exceptions;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para la API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOG =
      LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Maneja errores de validacion de forma (@Valid).
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException mae) {
    String mensaje = mae.getBindingResult().getFieldErrors().stream() //
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage()) //
        .collect(Collectors.joining("; "));
    LOG.warn("Error de validacion: {}", mensaje);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(mensaje));
  }

  /**
   * Maneja errores de validacion de negocio.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException iae) {
    LOG.warn("Error de ngocio: {}", iae.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(new ErrorResponse(iae.getMessage()));
  }

}

