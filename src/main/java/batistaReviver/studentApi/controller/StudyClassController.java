package batistaReviver.studentApi.controller;

import batistaReviver.studentApi.dto.StudyClassDto;
import batistaReviver.studentApi.model.StudyClass;
import batistaReviver.studentApi.service.StudyClassService;
import java.util.List; // Import List
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link StudyClass} entities.
 *
 * <p>Provides API endpoints for creating, retrieving, deleting, and managing professor assignments
 * for study classes. All endpoints are mapped under the "/study-classes" base path.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/study-classes")
@RequiredArgsConstructor
public class StudyClassController {

  private final StudyClassService studyClassService;

  /** DTO for creating a new StudyClass. */
  public record CreateStudyClassRequest(int year, int semester, Long courseId, Long professorId) {}

  /** DTO for assigning a professor to a StudyClass. */
  public record AssignProfessorRequest(Long professorId) {}

  /**
   * Handles HTTP GET requests to retrieve all study classes.
   *
   * @return A {@link ResponseEntity} containing a list of all {@link StudyClassDto} objects and an
   *     OK status.
   */
  @GetMapping
  public ResponseEntity<List<StudyClassDto>> getAllStudyClasses() {
    return ResponseEntity.ok(studyClassService.getAllStudyClasses());
  }

  /**
   * Handles HTTP GET requests to retrieve all study classes for a specific course.
   *
   * @param courseId The ID of the course to filter classes by.
   * @return A {@link ResponseEntity} containing a list of {@link StudyClassDto} objects and an OK
   *     status.
   */
  @GetMapping("/course/{courseId}")
  public ResponseEntity<List<StudyClassDto>> findStudyClassesByCourse(@PathVariable Long courseId) {
    return ResponseEntity.ok(studyClassService.getClassesByCourse(courseId));
  }

  /**
   * Handles HTTP GET requests to retrieve all study classes taught by a specific professor.
   *
   * @param professorId The ID of the professor to filter classes by.
   * @return A {@link ResponseEntity} containing a list of {@link StudyClassDto} objects and an OK
   *     status.
   */
  @GetMapping("/professor/{professorId}")
  public ResponseEntity<List<StudyClassDto>> findStudyClassesByProfessor(
      @PathVariable Long professorId) {
    return ResponseEntity.ok(studyClassService.getClassesByProfessor(professorId));
  }

  /**
   * Handles HTTP GET requests to retrieve a single study class by its ID.
   *
   * @param id The ID of the study class to retrieve.
   * @return A {@link ResponseEntity} containing the found {@link StudyClassDto} and an OK status.
   */
  @GetMapping("/{id}")
  public ResponseEntity<StudyClassDto> getStudyClassById(@PathVariable Long id) {
    StudyClassDto studyClass = studyClassService.getStudyClassById(id);
    return ResponseEntity.ok(studyClass);
  }

  /**
   * Handles HTTP POST requests to create a new study class.
   *
   * @param request A {@link CreateStudyClassRequest} containing the details for the new study
   *     class.
   * @return A {@link ResponseEntity} containing the newly created {@link StudyClassDto} and a
   *     CREATED status.
   */
  @PostMapping
  public ResponseEntity<StudyClassDto> createStudyClass(
      @RequestBody CreateStudyClassRequest request) {
    StudyClassDto createdClass =
        studyClassService.createStudyClass(
            request.year(), request.semester(), request.courseId(), request.professorId());
    return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
  }

  /**
   * Handles HTTP PUT requests to assign a professor to a study class.
   *
   * @param classId The ID of the study class.
   * @param request A {@link AssignProfessorRequest} containing the ID of the professor to assign.
   * @return A {@link ResponseEntity} containing the updated {@link StudyClassDto} and an OK status.
   */
  @PutMapping("/{classId}/professor")
  public ResponseEntity<StudyClassDto> assignProfessor(
      @PathVariable Long classId, @RequestBody AssignProfessorRequest request) {
    StudyClassDto updatedClass = studyClassService.assignProfessor(classId, request.professorId());
    return ResponseEntity.ok(updatedClass);
  }

  /**
   * Handles HTTP DELETE requests to unassign a professor from a study class.
   *
   * @param classId The ID of the study class.
   * @return A {@link ResponseEntity} containing the updated {@link StudyClassDto} and an OK status.
   */
  @DeleteMapping("/{classId}/professor")
  public ResponseEntity<StudyClassDto> unassignProfessor(@PathVariable Long classId) {
    StudyClassDto updatedClass = studyClassService.unassignProfessor(classId);
    return ResponseEntity.ok(updatedClass);
  }

  /**
   * Handles HTTP DELETE requests to delete a study class by its ID.
   *
   * @param id The ID of the study class to delete.
   * @return A {@link ResponseEntity} with NO_CONTENT status indicating successful deletion.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStudyClass(@PathVariable Long id) {
    studyClassService.deleteStudyClass(id);
    return ResponseEntity.noContent().build();
  }
}
