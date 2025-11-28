package batistaReviver.studentApi.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application.
 *
 * <p>This class is annotated with {@link ControllerAdvice}, which allows it to intercept and handle
 * exceptions thrown by any controller across the application. By centralizing exception handling,
 * it ensures consistent and meaningful HTTP responses for both system and custom errors.
 *
 * <p>Handled exceptions include:
 *
 * <ul>
 *   <li>{@link EntityNotFoundException} → returns {@link HttpStatus#NOT_FOUND} (404)
 *   <li>{@link EntityValidationException} → returns {@link HttpStatus#BAD_REQUEST} (400)
 *   <li>{@link ProfessorEnrolledException} → returns {@link HttpStatus#CONFLICT} (409)
 *   <li>{@link StudentEnrolledException} → returns {@link HttpStatus#CONFLICT} (409)
 *   <li>{@link StudyClassExistsException} → returns {@link HttpStatus#CONFLICT} (409)
 *   <li>{@link SubscriptionFoundException} → returns {@link HttpStatus#CONFLICT} (409)
 * </ul>
 *
 * <p>Each handler method wraps the exception message in a {@link ResponseEntity}, ensuring that
 * clients receive clear feedback about the error cause and the appropriate HTTP status.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles cases where an entity was not found in the database.
   *
   * @param ex the thrown {@link EntityNotFoundException}
   * @return a {@link ResponseEntity} with the exception message and HTTP 404 status
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * Handles cases where entity validation fails (e.g., invalid input or constraint violation).
   *
   * @param ex the thrown {@link EntityValidationException}
   * @return a {@link ResponseEntity} with the exception message and HTTP 400 status
   */
  @ExceptionHandler(EntityValidationException.class)
  public ResponseEntity<String> handleEntityValidation(EntityValidationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * Handles cases where a professor is already enrolled and cannot be reassigned.
   *
   * @param ex the thrown {@link ProfessorEnrolledException}
   * @return a {@link ResponseEntity} with the exception message and HTTP 409 status
   */
  @ExceptionHandler(ProfessorEnrolledException.class)
  public ResponseEntity<String> handleProfessorEnrolled(ProfessorEnrolledException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  /**
   * Handles cases where a student is already enrolled and cannot be added again.
   *
   * @param ex the thrown {@link StudentEnrolledException}
   * @return a {@link ResponseEntity} with the exception message and HTTP 409 status
   */
  @ExceptionHandler(StudentEnrolledException.class)
  public ResponseEntity<String> handleStudentEnrolled(StudentEnrolledException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  /**
   * Handles cases where a study class already exists and a duplicate cannot be created.
   *
   * @param ex the thrown {@link StudyClassExistsException}
   * @return a {@link ResponseEntity} with the exception message and HTTP 409 status
   */
  @ExceptionHandler(StudyClassExistsException.class)
  public ResponseEntity<String> handleStudyClassExists(StudyClassExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  /**
   * Handles cases where a subscription already exists for a given entity.
   *
   * @param ex the thrown {@link SubscriptionFoundException}
   * @return a {@link ResponseEntity} with the exception message and HTTP 409 status
   */
  @ExceptionHandler(SubscriptionFoundException.class)
  public ResponseEntity<String> handleSubscriptionFound(SubscriptionFoundException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseApp> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    Map<String, String> map = new HashMap<>();
    for (FieldError fe : e.getBindingResult().getFieldErrors()) {
      map.put(fe.getField(), fe.getDefaultMessage());
    }
    return new ResponseEntity<>(
        new ErrorResponseApp(
            LocalDateTime.now(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            HttpStatus.UNPROCESSABLE_ENTITY.name(),
            request.getMethod(),
            request.getRequestURI(),
            map,
            e.getMessage()),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }
}
