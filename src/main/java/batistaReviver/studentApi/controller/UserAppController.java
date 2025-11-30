package batistaReviver.studentApi.controller;

import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.service.UserAppService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link UserApp} entities.
 * <p>
 * Provides endpoints for registering new users and listing existing ones.
 * Note: The service currently forces the role to USER.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/userApp")
@RequiredArgsConstructor
public class UserAppController {

  private final UserAppService userAppService;

  /**
   * Retrieves a list of all registered users.
   *
   * @return A list of {@link UserApp} entities.
   */
  @GetMapping
  public ResponseEntity<List<UserApp>> getAllUsers() {
    return ResponseEntity.ok(userAppService.fetchAllUserApps());
  }

  /**
   * Creates a new user.
   *
   * @param userApp The user details.
   * @return The created {@link UserApp}.
   */
  @PostMapping
  public ResponseEntity<UserApp> createUser(@Valid @RequestBody UserApp userApp) {
    // Note: UserAppService currently overwrites any passed role to Role.USER
    return new ResponseEntity<>(userAppService.createUserApp(userApp), HttpStatus.CREATED);
  }
}