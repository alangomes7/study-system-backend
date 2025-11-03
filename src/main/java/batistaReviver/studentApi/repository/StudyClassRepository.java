package batistaReviver.studentApi.repository;

import batistaReviver.studentApi.model.StudyClass;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link StudyClass} entity.
 *
 * <p>This interface provides methods for CRUD operations and custom queries for managing {@link
 * StudyClass} entities. It includes methods to find classes by professor, check for existing
 * classes to prevent duplicates, and verify associations with courses and professors.
 */
@Repository
public interface StudyClassRepository extends JpaRepository<StudyClass, Long> {

  /**
   * Finds all study classes taught by a specific professor.
   *
   * @param professorId The ID of the professor.
   * @return A list of {@link StudyClass} entities taught by the professor.
   */
  List<StudyClass> findByProfessorId(Long professorId);

  /**
   * Finds all study classes for a specific course.
   *
   * @param courseId The ID of the course.
   * @return A list of {@link StudyClass} entities for the course.
   */
  List<StudyClass> findByCourseId(Long courseId);

  /**
   * Checks if any study class is associated with a specific professor.
   *
   * @param professorId The ID of the professor.
   * @return {@code true} if the professor is assigned to at least one study class, {@code false}
   *     otherwise.
   */
  boolean existsByProfessorId(Long professorId);

  /**
   * Checks if a class for the same course, year, and semester already exists.
   *
   * @param courseId The ID of the course.
   * @param year The academic year.
   * @param semester The semester.
   * @return {@code true} if a class already exists, {@code false} otherwise.
   */
  boolean existsByCourseIdAndYearAndSemester(Long courseId, int year, int semester);

  /**
   * Checks if any study class is associated with a specific course.
   *
   * @param courseId The ID of the course.
   * @return {@code true} if the course has at least one study class, {@code false} otherwise.
   */
  boolean existsByCourseId(Long courseId);
}
