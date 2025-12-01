package batistaReviver.studentApi.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<ErrorResponseApp> buildError(
          Exception e, HttpStatus status, HttpServletRequest request, Map<String,String> fieldErrors) {

    ErrorResponseApp error = new ErrorResponseApp(
            LocalDateTime.now(),
            status.value(),
            status.name(),
            request.getMethod(),
            request.getRequestURI(),
            fieldErrors,
            e.getMessage()
    );

    return new ResponseEntity<>(error, status);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponseApp> handleEntityNotFound(
          EntityNotFoundException e, HttpServletRequest request) {
    return buildError(e, HttpStatus.NOT_FOUND, request, null);
  }

  @ExceptionHandler(EntityValidationException.class)
  public ResponseEntity<ErrorResponseApp> handleEntityValidation(
          EntityValidationException e, HttpServletRequest request) {
    return buildError(e, HttpStatus.BAD_REQUEST, request, null);
  }

  @ExceptionHandler({
          ProfessorEnrolledException.class,
          StudentEnrolledException.class,
          StudyClassExistsException.class,
          SubscriptionFoundException.class
  })
  public ResponseEntity<ErrorResponseApp> handleConflict(
          RuntimeException e, HttpServletRequest request) {
    return buildError(e, HttpStatus.CONFLICT, request, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseApp> handleValidationErrors(
          MethodArgumentNotValidException e, HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();
    for (FieldError fe : e.getBindingResult().getFieldErrors()) {
      errors.put(fe.getField(), fe.getDefaultMessage());
    }

    return buildError(e, HttpStatus.UNPROCESSABLE_ENTITY, request, errors);
  }

  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public ResponseEntity<ErrorResponseApp> handleSQLIntegrity(
          SQLIntegrityConstraintViolationException e, HttpServletRequest request) {

    return buildError(e, HttpStatus.UNPROCESSABLE_ENTITY, request, null);
  }

  // fallback (unexpected)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseApp> handleGeneric(
          Exception e, HttpServletRequest request) {

    return buildError(e, HttpStatus.INTERNAL_SERVER_ERROR, request, null);
  }
}
