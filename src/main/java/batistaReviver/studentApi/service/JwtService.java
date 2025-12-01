package batistaReviver.studentApi.service;

import batistaReviver.studentApi.exception.JwtAuthenticationException;
import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.util.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service responsible for managing JSON Web Tokens (JWT).
 *  * <p>
 * This service handles the lifecycle of access tokens, including:
 * <ul>
 * <li>Generation (Signing)</li>
 * <li>Parsing (Extracting claims)</li>
 * <li>Validation (Checking signature and expiration)</li>
 * </ul>
 * It uses the HMAC-SHA algorithm for signing tokens.
 */
@Service
public class JwtService {

  private final SecretKey secretKey;

  private final long accessTokenExpiration;

  /**
   * Constructs the JwtService with configuration values.
   *
   * @param secret                The secret string used to sign the tokens. Must be at least 32 characters for HS256.
   * @param accessTokenExpiration The expiration time for access tokens in <strong>seconds</strong>.
   */
  public JwtService(
          @Value("${api.security.token.secret}") String secret,
          @Value("${jwt.security.accessTokenExpiration}") String accessTokenExpiration
  ) {
    // secret must be at least 32 characters for HS256
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.accessTokenExpiration = Long.parseLong(accessTokenExpiration);
  }

  /**
   * Generates a signed JWT access token for a specific user.
   *
   * @param userApp The user entity for whom the token is generated.
   * @return A String representation of the JWT (Header.Payload.Signature).
   */
  public String generateAccessToken(UserApp userApp) {
    return generateToken(userApp, accessTokenExpiration);
  }

  /**
   * Internal helper to build the JWT.
   * <p>
   * Adds custom claims:
   * <ul>
   * <li><strong>Subject:</strong> The User ID.</li>
   * <li><strong>name:</strong> The User's full name.</li>
   * <li><strong>role:</strong> The User's role (USER/ADMIN).</li>
   * </ul>
   *
   * @param userApp           The user.
   * @param expirationSeconds Time in seconds until the token expires.
   * @return The compact JWT string.
   */
  private String generateToken(UserApp userApp, long expirationSeconds) {
    long now = System.currentTimeMillis();

    return Jwts.builder()
            .subject(userApp.getId().toString())
            .claim("name", userApp.getName())
            .claim("role", userApp.getRole().name())
            .issuedAt(new Date(now))
            .expiration(new Date(now + expirationSeconds * 1000))
            .signWith(secretKey)
            .compact();
  }

  /**
   * Validates the token and throws specific exceptions if invalid.
   * <p>
   * This method is designed to be used by filters where specific error messages
   * are required for the HTTP response. It catches standard JJWT exceptions and
   * rethrows them as a custom {@link JwtAuthenticationException}.
   *
   * @param token The JWT string to validate.
   * @throws JwtAuthenticationException If the token is expired, malformed, has an invalid signature, or is empty.
   */
  public void validateOrThrow(String token) {
    try {
      Jwts.parser()
              .verifyWith(secretKey)
              .build()
              .parseSignedClaims(token);
    } catch (ExpiredJwtException e) {
      throw new JwtAuthenticationException("Token has expired");
    } catch (SignatureException e) {
      throw new JwtAuthenticationException("Invalid token signature");
    } catch (MalformedJwtException e) {
      throw new JwtAuthenticationException("Malformed JWT token");
    } catch (UnsupportedJwtException e) {
      throw new JwtAuthenticationException("Unsupported JWT token");
    } catch (IllegalArgumentException e) {
      throw new JwtAuthenticationException("Token is missing or empty");
    }
  }

  /**
   * Checks if a token is valid without throwing specific exceptions.
   *
   * @param token The JWT string.
   * @return {@code true} if the token is valid and not expired; {@code false} otherwise.
   */
  public boolean validateToken(String token) {
    try {
      Claims claims = getClaims(token);
      return claims.getExpiration().after(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Extracts the payload (Claims) from the JWT.
   *
   * @param token The JWT string.
   * @return The {@link Claims} object containing the token data.
   */
  public Claims getClaims(String token) {
    return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  /**
   * Extracts the User ID (Subject) from the token.
   *
   * @param token The JWT string.
   * @return The user ID as a Long.
   */
  public Long getUserIdFromToken(String token) {
    return Long.parseLong(getClaims(token).getSubject());
  }

  /**
   * Extracts the Role from the token.
   *
   * @param token The JWT string.
   * @return The {@link Role} enum value.
   */
  public Role getRoleFromToken(String token) {
    String role = getClaims(token).get("role", String.class);
    return Role.valueOf(role);
  }
}