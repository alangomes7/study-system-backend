package batistaReviver.studentApi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents the enrollment of a Student in a specific StudyClass.
 *
 * <p>This entity acts as a many-to-many join table between the Student and StudyClass entities. It
 * records the exact time of the subscription.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {

  /** The unique identifier for the subscription. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The timestamp of when the subscription was created. This value is automatically set by the
   * database upon creation.
   */
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime date;

  /**
   * The student who is subscribed. This establishes a many-to-one relationship with the Student
   * entity.
   */
  @NotNull(message = "Student is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  /**
   * The specific class to which the student is subscribed. This establishes a many-to-one
   * relationship with the StudyClass entity.
   */
  @NotNull(message = "Study Class is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_class_id", nullable = false)
  private StudyClass studyClass;

  /**
   * Constructs a new Subscription instance.
   *
   * @param student The student enrolling.
   * @param studyClass The specific class offering being enrolled in.
   */
  public Subscription(Student student, StudyClass studyClass) {
    this.student = student;
    this.studyClass = studyClass;
  }

  /**
   * Returns if the Subscription instance is related to given Student
   *
   * @param student The student enrolling.
   */
  public boolean studentSubscription(Student student) {
    return Objects.equals(student.getId(), this.getStudent().getId());
  }

  /**
   * Returns if the Subscription instance is related to given StudyClass
   *
   * @param studyClass The associated studyClass.
   */
  public boolean studyClassSubscription(StudyClass studyClass) {
    return Objects.equals(studyClass.getId(), this.getStudyClass().getId());
  }
}
