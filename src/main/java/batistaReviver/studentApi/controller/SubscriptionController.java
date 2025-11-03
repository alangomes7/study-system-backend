package batistaReviver.studentApi.controller;

import batistaReviver.studentApi.dto.SubscriptionDto;
import batistaReviver.studentApi.model.Subscription;
import batistaReviver.studentApi.service.SubscriptionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Subscription} entities.
 *
 * <p>Provides API endpoints for creating, retrieving, and deleting subscriptions, which represent
 * the enrollment of a {@link batistaReviver.studentApi.model.Student} in a {@link
 * batistaReviver.studentApi.model.StudyClass}.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  /** DTO for creating a new Subscription. */
  public record CreateSubscriptionRequest(Long studentId, Long studyClassId) {}

  /**
   * Handles HTTP GET requests to retrieve all subscriptions, optionally filtered by student or
   * study class.
   *
   * @param studentId Optional ID of the student to filter subscriptions by.
   * @param studyClassId Optional ID of the study class to filter subscriptions by.
   * @return A {@link ResponseEntity} containing a list of {@link SubscriptionDto} objects and an OK
   *     status.
   */
  @GetMapping
  public ResponseEntity<List<SubscriptionDto>> findSubscriptions(
      @RequestParam(required = false) Long studentId,
      @RequestParam(required = false) Long studyClassId) {

    // This single method now handles all GET cases for better flexibility.
    if (studentId != null) {
      return ResponseEntity.ok(subscriptionService.getStudentHistory(studentId));
    }

    if (studyClassId != null) {
      return ResponseEntity.ok(subscriptionService.getSubscriptionsByClass(studyClassId));
    }

    return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
  }

  /**
   * Handles HTTP POST requests to create a new subscription.
   *
   * @param request A {@link CreateSubscriptionRequest} containing the IDs of the student and study
   *     class.
   * @return A {@link ResponseEntity} containing the newly created {@link SubscriptionDto} and a
   *     CREATED status.
   */
  @PostMapping
  public ResponseEntity<SubscriptionDto> createSubscription(
      @RequestBody CreateSubscriptionRequest request) {
    SubscriptionDto createdSubscription =
        subscriptionService.createSubscription(request.studentId(), request.studyClassId());
    return new ResponseEntity<>(createdSubscription, HttpStatus.CREATED);
  }

  /**
   * Handles HTTP DELETE requests to delete a subscription by its ID.
   *
   * @param id The ID of the subscription to delete.
   * @return A {@link ResponseEntity} with NO_CONTENT status indicating successful deletion.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
    subscriptionService.deleteSubscription(id);
    return ResponseEntity.noContent().build();
  }
}
