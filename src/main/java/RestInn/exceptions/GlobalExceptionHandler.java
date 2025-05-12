package RestInn.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Maneja excepciones generales (error interno)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(new ErrorDetails("Error interno del servidor", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Maneja excepciones cuando un cliente no se encuentra
    @ExceptionHandler(ClienteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleClienteNotFoundException(ClienteNotFoundException ex) {
        return new ResponseEntity<>(new ErrorDetails("Cliente no encontrado", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // Maneja excepciones cuando los datos de la solicitud son inválidos
    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException ex) {
        return new ResponseEntity<>(new ErrorDetails("Datos inválidos", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Maneja excepciones cuando la solicitud está mal formada
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(new ErrorDetails("Solicitud mal formada", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Maneja excepciones cuando el cliente no tiene permisos suficientes
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(new ErrorDetails("Acceso no autorizado", ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
