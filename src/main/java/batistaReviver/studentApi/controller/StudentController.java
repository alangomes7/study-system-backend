package batistaReviver.studentApi.controller;

import batistaReviver.studentApi.model.Student;
import batistaReviver.studentApi.service.StudentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Student} entities. Provides endpoints for CRUD operations on
 * students.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

  private final StudentService studentService;

  /**
   * Retrieves a list of all students.
   *
   * @return A list of all {@link Student} entities.
   */
  @GetMapping
  public List<Student> getAllStudents() {
    return studentService.getAllStudents();
  }

  /**
   * Retrieves a specific student by their ID.
   *
   * @param id The ID of the student to retrieve.
   * @return A {@link ResponseEntity} containing the student if found.
   * @throws batistaReviver.studentApi.exception.EntityNotFoundException if the student is not
   *     found.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
    return ResponseEntity.ok(studentService.getStudentById(id));
  }

  /**
   * Create a new student.
   *
   * @param student The student object to be added.
   * @return A {@link ResponseEntity} containing the created student and HTTP status 201 (Created).
   */
  @PostMapping
  public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
    return new ResponseEntity<>(studentService.addStudent(student), HttpStatus.CREATED);
  }

  /**
   * Modifies an existing student.
   *
   * @param id The ID of the student to modify.
   * @param studentDetails The new details for the student.
   * @return A {@link ResponseEntity} containing the updated student.
   * @throws batistaReviver.studentApi.exception.EntityNotFoundException if the student is not
   *     found.
   */
  @PutMapping("/{id}")
  public ResponseEntity<Student> updateStudent(
      @PathVariable Long id, @Valid @RequestBody Student studentDetails) {
    return ResponseEntity.ok(studentService.modifyStudent(id, studentDetails));
  }

  /**
   * Removes a student by their ID.
   *
   * @param id The ID of the student to remove.
   * @return A {@link ResponseEntity} with HTTP status 204 (No Content).
   * @throws batistaReviver.studentApi.exception.EntityNotFoundException if the student is not
   *     found.
   * @throws batistaReviver.studentApi.exception.StudentEnrolledException if the student is enrolled
   *     in any class.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> removeStudent(@PathVariable Long id) {
    studentService.removeStudent(id);
    return ResponseEntity.noContent().build();
  }
}
