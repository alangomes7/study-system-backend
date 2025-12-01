package batistaReviver.studentApi.repository;

import batistaReviver.studentApi.model.UserApp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link UserApp} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and database interaction for the application's users.
 */
public interface UserAppRepository extends JpaRepository<UserApp, Long> {

  /**
   * Retrieves a user based on their email address.
   * <p>
   * This is a Spring Data JPA derived query method.
   *
   * @param email The email address to search for.
   * @return An {@link Optional} containing the {@link UserApp} if found, or empty if not.
   */
  Optional<UserApp> findByEmail(String email);
}