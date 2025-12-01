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

@Service
public class JwtService {

  private final SecretKey secretKey;

  private final long accessTokenExpiration;

  public JwtService(
          @Value("${api.security.token.secret}") String secret,
          @Value("${jwt.security.accessTokenExpiration}") String accessTokenExpiration
  ) {
    // secret must be at least 32 characters for HS256
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.accessTokenExpiration = Long.parseLong(accessTokenExpiration);
  }

  public String generateAccessToken(UserApp userApp) {
    return generateToken(userApp, accessTokenExpiration);
  }

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
   * This allows the JwtAuthenticationFilter to return precise error messages.
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

  public boolean validateToken(String token) {
    try {
      Claims claims = getClaims(token);
      return claims.getExpiration().after(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public Claims getClaims(String token) {
    return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public Long getUserIdFromToken(String token) {
    return Long.parseLong(getClaims(token).getSubject());
  }

  public Role getRoleFromToken(String token) {
    String role = getClaims(token).get("role", String.class);
    return Role.valueOf(role);
  }
}