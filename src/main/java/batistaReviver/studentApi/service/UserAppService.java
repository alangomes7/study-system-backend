package batistaReviver.studentApi.service;

import batistaReviver.studentApi.exception.EntityNotFoundException;
import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.repository.UserAppRepository;
import batistaReviver.studentApi.util.Role;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for managing User entities and handling authentication lookup.
 * <p>
 * This class implements Spring Security's {@link UserDetailsService}, allowing
 * the authentication manager to load user details from the database during login.
 */
@AllArgsConstructor
@Service
public class UserAppService implements UserDetailsService {

  private final UserAppRepository userAppRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Registers a new user in the system.
   * <p>
   * This method performs the following:
   * <ol>
   * <li>Checks if the email is already registered.</li>
   * <li>Encodes the raw password using {@link PasswordEncoder}.</li>
   * <li>Sets a default role of {@code USER} if none is provided.</li>
   * <li>Saves the entity to the database.</li>
   * </ol>
   *
   * @param userApp The user entity to create.
   * @return The saved {@link UserApp} entity.
   * @throws RuntimeException If a user with the given email already exists.
   */
  public UserApp createUserApp(UserApp userApp) {
    if (userAppRepository.findByEmail(userApp.getEmail()).isPresent()) {
      throw new RuntimeException("User already registered!");
    }

    userApp.setPassword(passwordEncoder.encode(userApp.getPassword()));

    if (userApp.getRole() == null) {
      userApp.setRole(Role.USER);
    }

    return userAppRepository.save(userApp);
  }

  /**
   * Retrieves all registered users.
   *
   * @return A list of all {@link UserApp} entities.
   */
  public List<UserApp> fetchAllUserApps() {
    return userAppRepository.findAll();
  }

  /**
   * Loads a user by their username (email) for Spring Security authentication.
   * <p>
   * This method translates the domain {@link UserApp} into a Spring Security
   * compatible {@link UserDetails} object.
   *
   * @param email The email address of the user to load.
   * @return A {@link UserDetails} object containing the email and hashed password.
   * @throws UsernameNotFoundException If the user is not found (rethrown as {@link EntityNotFoundException}).
   */
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    UserApp userApp =
            userAppRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

    // Note: Authorities are returned as empty here because roles are primarily
    // handled via the JWT token claims in the JwtAuthenticationFilter.
    return new User(userApp.getEmail(), userApp.getPassword(), Collections.emptyList());
  }
}