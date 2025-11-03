package batistaReviver.studentApi.service;

import batistaReviver.studentApi.dto.StudyClassDto;
import batistaReviver.studentApi.exception.EntityNotFoundException;
import batistaReviver.studentApi.exception.EntityValidationException;
import batistaReviver.studentApi.exception.ProfessorEnrolledException;
import batistaReviver.studentApi.exception.SubscriptionFoundException;
import batistaReviver.studentApi.model.Course;
import batistaReviver.studentApi.model.Professor;
import batistaReviver.studentApi.model.StudyClass;
import batistaReviver.studentApi.repository.CourseRepository;
import batistaReviver.studentApi.repository.ProfessorRepository;
import batistaReviver.studentApi.repository.StudyClassRepository;
import batistaReviver.studentApi.repository.SubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyClassService {

  private final StudyClassRepository studyClassRepository;
  private final CourseRepository courseRepository;
  private final ProfessorRepository professorRepository;
  private final SubscriptionRepository subscriptionRepository;

  /**
   * Creates a new StudyClass for a given course, year, and semester. A professor can be optionally
   * assigned.
   *
   * @param year The academic year.
   * @param semester The semester.
   * @param courseId The ID of the course.
   * @param professorId The ID of the professor (optional, can be null).
   * @return The newly created {@link StudyClassDto}.
   * @throws EntityNotFoundException if the course or professor (if provided) is not found.
   */
  @Transactional
  public StudyClassDto createStudyClass(int year, int semester, Long courseId, Long professorId) {
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(
                () -> new EntityNotFoundException("Course with id = " + courseId + " not found."));

    Professor professor = null;

    if (professorId != null) {
      professor =
          professorRepository
              .findById(professorId)
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Professor with id = " + professorId + " not found."));
    }

    StudyClass newStudyClass = new StudyClass(year, semester, course, professor);
    // Set a temporary code; we need the ID to make it final.
    newStudyClass.setClassCode("TEMP-" + System.currentTimeMillis());
    // First save to generate the entity ID.
    StudyClass persistedStudyClass = studyClassRepository.save(newStudyClass);

    // Now, generate the final, unique class code using the generated ID.
    String finalClassCode = generateClassCode(course, year, semester, persistedStudyClass.getId());
    persistedStudyClass.setClassCode(finalClassCode);

    return new StudyClassDto(persistedStudyClass); // The transaction will commit the final state.
  }

  /**
   * Generates a unique code for a class based on the course name, year, and semester.
   *
   * @param course The course.
   * @param year The academic year.
   * @param semester The semester.
   * @param id The unique ID of the class.
   * @return A generated string code (e.g., "IA20241-1").
   */
  private String generateClassCode(Course course, int year, int semester, Long id) {
    String[] nameParts = course.getName().split(" ");
    StringBuilder abbreviation = new StringBuilder();
    for (String part : nameParts) {
      if (!part.isEmpty()) {
        abbreviation.append(part.charAt(0));
      }
    }
    return (abbreviation.toString() + year + semester).toUpperCase() + "-" + id;
  }

  /**
   * Retrieves all study classes.
   *
   * @return A list of all {@link StudyClassDto}s.
   */
  @Transactional(readOnly = true)
  public List<StudyClassDto> getAllStudyClasses() {
    return studyClassRepository.findAll().stream().map(StudyClassDto::new).toList();
  }

  /**
   * Retrieves a single study class by its ID.
   *
   * @param id The ID of the class.
   * @return The found {@link StudyClassDto}.
   * @throws EntityNotFoundException if no class is found with the given ID.
   */
  @Transactional(readOnly = true)
  public StudyClassDto getStudyClassById(Long id) {
    StudyClass studyClass =
        studyClassRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("StudyClass with id = " + id + " not found."));
    return new StudyClassDto(studyClass);
  }

  /**
   * Retrieves all study classes taught by a specific professor.
   *
   * @param professorId The ID of the professor.
   * @return A list of {@link StudyClassDto}s.
   * @throws EntityNotFoundException if no professor is found with the given ID.
   */
  @Transactional(readOnly = true)
  public List<StudyClassDto> getClassesByProfessor(Long professorId) {
    if (!professorRepository.existsById(professorId)) {
      throw new EntityNotFoundException("Professor with id = " + professorId + " not found.");
    }
    return studyClassRepository.findByProfessorId(professorId).stream()
        .map(StudyClassDto::new)
        .toList();
  }

  /**
   * Retrieves all study classes for a specific course.
   *
   * @param courseId The ID of the course.
   * @return A list of {@link StudyClassDto}s.
   * @throws EntityNotFoundException if no course is found with the given ID.
   */
  @Transactional(readOnly = true)
  public List<StudyClassDto> getClassesByCourse(Long courseId) {
    if (!courseRepository.existsById(courseId)) {
      throw new EntityNotFoundException("Course with id = " + courseId + " not found.");
    }
    return studyClassRepository.findByCourseId(courseId).stream().map(StudyClassDto::new).toList();
  }

  /**
   * Assigns a professor to a study class that does not currently have one.
   *
   * @param classId The ID of the study class.
   * @param professorId The ID of the professor to assign.
   * @return An updated {@link StudyClassDto} with the new professor.
   * @throws EntityNotFoundException if the class or professor is not found.
   * @throws ProfessorEnrolledException if the class already has a professor assigned.
   */
  @Transactional
  public StudyClassDto assignProfessor(Long classId, Long professorId) {
    StudyClass studyClass =
        studyClassRepository
            .findById(classId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("StudyClass with id = " + classId + " not found."));

    if (studyClass.getProfessor() != null) {
      throw new ProfessorEnrolledException(
          "StudyClass with id = " + classId + " already has a professor assigned.");
    }

    Professor professor =
        professorRepository
            .findById(professorId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Professor with id = " + professorId + " not found."));

    studyClass.setProfessor(professor);
    return new StudyClassDto(studyClass); // Transaction will handle the save.
  }

  /**
   * Unassigns a professor from a study class.
   *
   * @param classId The ID of the study class.
   * @return An updated {@link StudyClassDto} with the professor removed.
   * @throws EntityNotFoundException if the class is not found.
   * @throws EntityValidationException if the class does not have a professor assigned.
   */
  @Transactional
  public StudyClassDto unassignProfessor(Long classId) {
    StudyClass studyClass =
        studyClassRepository
            .findById(classId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("StudyClass with id = " + classId + " not found."));

    if (studyClass.getProfessor() == null) {
      throw new EntityValidationException(
          "StudyClass with id = " + classId + " does not have a professor assigned.");
    }

    studyClass.setProfessor(null);
    return new StudyClassDto(studyClass); // Transaction will handle the save.
  }

  /**
   * Deletes a StudyClass by its ID.
   *
   * @param id The ID of the class to delete.
   * @throws EntityNotFoundException if no class is found with the given ID.
   * @throws SubscriptionFoundException if the class has any student subscriptions.
   */
  @Transactional
  public void deleteStudyClass(Long id) {
    StudyClass studyClass =
        studyClassRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("StudyClass with id = " + id + " not found."));

    if (subscriptionRepository.existsByStudyClassId(id)) {
      throw new SubscriptionFoundException("Study class has subscriptions and cannot be removed.");
    }

    if (studyClass.getProfessor() != null) {
      throw new ProfessorEnrolledException("Study class has professor and cannot be removed.");
    }

    studyClassRepository.save(studyClass); // Ensure the relationship is severed before deleting.
    studyClassRepository.deleteById(id);
  }
}
