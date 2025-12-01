package batistaReviver.studentApi.security;

import org.springframework.http.HttpMethod;

/**
 * Configuration class that defines API endpoint constants.
 * <p>
 * This class centralizes the URL patterns and HTTP methods used for
 * configuring security rules in {@link SecurityConfig}.
 */
public class EndpointConfig {

  /**
   * Endpoints accessible by anyone (no authentication required).
   */
  public static final Endpoint[] PUBLIC_ENDPOINTS = {
          new Endpoint(HttpMethod.GET, "/courses/**"),
          new Endpoint(HttpMethod.POST, "/authentication/**"),
  };

  /**
   * Endpoints accessible by users with either 'USER' or 'ADMIN' roles.
   */
  public static final Endpoint[] USER_OR_ADMIN_ENDPOINTS = {
          new Endpoint(HttpMethod.GET, "/students/**"),
          new Endpoint(HttpMethod.GET, "/professors/**"),
          new Endpoint(HttpMethod.GET, "/study-classes/**"),
          new Endpoint(HttpMethod.GET, "/manage/**")
  };

  /**
   * Endpoints accessible ONLY by users with the 'ADMIN' role.
   */
  public static final Endpoint[] ADMIN_ENDPOINTS = {
          new Endpoint(HttpMethod.DELETE, "/students/**"),
          new Endpoint(HttpMethod.DELETE, "/professors/**"),
          new Endpoint(HttpMethod.DELETE, "/study-classes/**"),
          new Endpoint(HttpMethod.DELETE, "/manage/**")
  };

  /**
   * Simple record to hold the HTTP Method and URL pattern.
   */
  public record Endpoint(HttpMethod method, String pattern) {}
}