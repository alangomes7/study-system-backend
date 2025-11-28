package batistaReviver.studentApi.service;

import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.util.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${api.security.token.secret}")
  private String secret;

  @Value("${jwt.security.accessTokenExpiration}")
  private String accessTokenExpiration;

  public String generateAccessToken(UserApp userApp) {
    final long tokenExpiration = Long.parseLong(accessTokenExpiration); // 2 horas
    return generateToken(userApp, tokenExpiration);
  }

  private String generateToken(UserApp userApp, long tokenExpiration) {
    return Jwts.builder()
        .subject(userApp.getId().toString())
        .claim("name", userApp.getName())
        .claim("role", userApp.getRole())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Claims claims = getClaims(token);
      boolean valid = claims.getExpiration().after(new Date());
      return valid;
    } catch (JwtException e) {
      return false;
    }
  }

  public Claims getClaims(String token) {
    Claims claims =
        Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    return claims;
  }

  public Long getUserIdFromToken(String token) {
    return Long.valueOf(getClaims(token).getSubject());
  }

  public Role getRoleFromToken(String token) {
    return Role.valueOf(getClaims(token).get("role", String.class));
  }
}
