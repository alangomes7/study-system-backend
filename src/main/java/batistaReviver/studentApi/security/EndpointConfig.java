package batistaReviver.studentApi.security;

import org.springframework.http.HttpMethod;

/**
 * Configuration class that defines API endpoint constants.
 * <p>
 * This class centralizes the URL patterns and HTTP methods used for
 * configuring security rules in {@link SecurityConfig}.
 */
public class EndpointConfig {

  // ========================================================================
  // PATH CONSTANTS
  // ========================================================================

  private static final String AUTH_PATH = "/authentication/**";
  private static final String COURSES_PATH = "/courses/**";
  private static final String STUDENTS_PATH = "/students/**";
  private static final String PROFESSORS_PATH = "/professors/**";
  private static final String CLASSES_PATH = "/study-classes/**";
  private static final String SUBSCRIPTIONS_PATH = "/subscriptions/**";
  private static final String MANAGE_PATH = "/manage/**";

  /**
   * Helper array of paths that share the exact same security logic
   * (Read/Write for Users, Update/Delete for Admins).
   */
  private static final String[] CORE_RESOURCES = {
          STUDENTS_PATH, PROFESSORS_PATH, CLASSES_PATH, SUBSCRIPTIONS_PATH, MANAGE_PATH
  };

  // ========================================================================
  // ENDPOINT GROUPS
  // ========================================================================

  /**
   * Endpoints accessible by anyone (no authentication required).
   */
  public static final Endpoint[] PUBLIC_ENDPOINTS = {
          new Endpoint(HttpMethod.GET, COURSES_PATH),
          new Endpoint(HttpMethod.POST, AUTH_PATH),
  };

  /**
   * Endpoints accessible by users with either 'USER' or 'ADMIN' roles.
   * Includes Read (GET) and Create (POST) operations.
   */
  public static final Endpoint[] USER_OR_ADMIN_ENDPOINTS = buildEndpoints(
          HttpMethod.GET,
          HttpMethod.POST
  );

  /**
   * Endpoints accessible ONLY by users with the 'ADMIN' role.
   * Includes Update (PUT) and Delete (DELETE) operations.
   */
  public static final Endpoint[] ADMIN_ENDPOINTS = buildEndpoints(
          HttpMethod.PUT,
          HttpMethod.DELETE
  );

  // ========================================================================
  // HELPER TYPES & METHODS
  // ========================================================================

  /**
   * Simple record to hold the HTTP Method and URL pattern.
   */
  public record Endpoint(HttpMethod method, String pattern) {}

  /**
   * Helper method to generate endpoints for multiple paths and methods to reduce verbosity.
   */
  private static Endpoint[] buildEndpoints(HttpMethod... methods) {
    java.util.List<Endpoint> list = new java.util.ArrayList<>();
    for (String path : EndpointConfig.CORE_RESOURCES) {
      for (HttpMethod method : methods) {
        list.add(new Endpoint(method, path));
      }
    }
    return list.toArray(new Endpoint[0]);
  }
}