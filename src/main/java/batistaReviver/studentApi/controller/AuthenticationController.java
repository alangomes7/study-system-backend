package batistaReviver.studentApi.controller;

import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.repository.UserAppRepository;
import batistaReviver.studentApi.service.JwtService;
import batistaReviver.studentApi.dto.TokenResponse;
import batistaReviver.studentApi.util.UserAppLogin;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller responsible for handling user authentication requests.
 * <p>
 * This controller provides endpoints for user login and token generation.
 * It interacts with the {@link AuthenticationManager} to verify credentials and
 * the {@link JwtService} to issue access tokens.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserAppRepository userAppRepository;

  /**
   * Authenticates a user and returns a JWT access token.
   * <p>
   * This method performs the following steps:
   * <ol>
   * <li>Validates the provided login credentials using {@link AuthenticationManager}.</li>
   * <li>Retrieves the full user details from the database via {@link UserAppRepository}.</li>
   * <li>Generates a JWT access token using {@link JwtService}.</li>
   * </ol>
   *
   * @param userAppLogin The DTO containing the user's email and password.
   * @param response     The HTTP servlet response object (can be used to manipulate headers/cookies).
   * @return A {@link ResponseEntity} containing the {@link TokenResponse} (access token, user ID, name, and role)
   * and an HTTP status of OK (200).
   * @throws org.springframework.security.core.AuthenticationException if authentication fails.
   * @throws java.util.NoSuchElementException if the user exists in auth context but not in the database.
   */
  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(
          @Valid @RequestBody UserAppLogin userAppLogin, HttpServletResponse response) {

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    userAppLogin.getEmail(), userAppLogin.getPassword()));

    UserApp userApp = userAppRepository.findByEmail(userAppLogin.getEmail()).orElseThrow();

    String accessToken = jwtService.generateAccessToken(userApp);

    return new ResponseEntity<>(
            new TokenResponse(
                    accessToken, userApp.getId(), userApp.getName(), userApp.getRole().name()),
            HttpStatus.OK);
  }
}