package batistaReviver.studentApi.controller;

import batistaReviver.studentApi.model.Professor;
import batistaReviver.studentApi.service.ProfessorService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Professor} entities.
 *
 * <p>Provides API endpoints for CRUD operations on professors.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/professors")
@RequiredArgsConstructor
public class ProfessorController {

  private final ProfessorService professorService;

  /**
   * Handles HTTP POST requests to create a new professor.
   *
   * @param professor The {@link Professor} object from the request body.
   * @return A {@link ResponseEntity} containing the newly created {@link Professor} and a CREATED
   *     status.
   */
  @PostMapping
  public ResponseEntity<Professor> createProfessor(@Valid @RequestBody Professor professor) {
    Professor createdProfessor = professorService.createProfessor(professor);
    return new ResponseEntity<>(createdProfessor, HttpStatus.CREATED);
  }

  /**
   * Handles HTTP GET requests to retrieve all professors.
   *
   * @return A {@link ResponseEntity} containing a list of all {@link Professor} entities and an OK
   *     status.
   */
  @GetMapping
  public ResponseEntity<List<Professor>> getAllProfessors() {
    return ResponseEntity.ok(professorService.getAllProfessors());
  }

  /**
   * Handles HTTP GET requests to retrieve a single professor by their ID.
   *
   * @param id The ID of the professor to retrieve.
   * @return A {@link ResponseEntity} containing the found {@link Professor} and an OK status.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Professor> getProfessorById(@PathVariable Long id) {
    return ResponseEntity.ok(professorService.getProfessorById(id));
  }

  /**
   * Handles HTTP PUT requests to update an existing professor.
   *
   * @param id The ID of the professor to update.
   * @param professorDetails The professor object with updated details from the request body.
   * @return A {@link ResponseEntity} containing the updated {@link Professor} and an OK status.
   */
  @PutMapping("/{id}")
  public ResponseEntity<Professor> updateProfessor(
      @PathVariable Long id, @Valid @RequestBody Professor professorDetails) {
    return ResponseEntity.ok(professorService.updateProfessor(id, professorDetails));
  }

  /**
   * Handles HTTP DELETE requests to remove a professor.
   *
   * @param id The ID of the professor to delete.
   * @return A {@link ResponseEntity} with NO_CONTENT status indicating successful deletion.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProfessor(@PathVariable Long id) {
    professorService.deleteProfessor(id);
    return ResponseEntity.noContent().build();
  }
}
