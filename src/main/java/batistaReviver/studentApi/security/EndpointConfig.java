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
          STUDENTS_PATH, PROFESSORS_PATH, CLASSES_PATH, MANAGE_PATH
  };

  /**
   * Helper array of paths that are accessible ONLY by Admins.
   */
  private static final String[] ADMIN_ONLY_RESOURCES = {
          SUBSCRIPTIONS_PATH
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
          CORE_RESOURCES,
          HttpMethod.GET,
          HttpMethod.POST
  );

  /**
   * Endpoints accessible ONLY by users with the 'ADMIN' role.
   * Includes Update (PUT) and Delete (DELETE) operations for Core Resources,
   * and ALL operations for Admin Only Resources.
   */
  public static final Endpoint[] ADMIN_ENDPOINTS = buildAdminEndpoints();

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
  private static Endpoint[] buildEndpoints(String[] paths, HttpMethod... methods) {
    java.util.List<Endpoint> list = new java.util.ArrayList<>();
    for (String path : paths) {
      for (HttpMethod method : methods) {
        list.add(new Endpoint(method, path));
      }
    }
    return list.toArray(new Endpoint[0]);
  }

  /**
   * Helper method to build the Admin endpoint list combining restricted core actions and fully restricted resources.
   */
  private static Endpoint[] buildAdminEndpoints() {
    java.util.List<Endpoint> list = new java.util.ArrayList<>();
    // Core resources: PUT, DELETE
    java.util.Collections.addAll(list, buildEndpoints(CORE_RESOURCES, HttpMethod.PUT, HttpMethod.DELETE));
    // Admin only resources: ALL methods
    java.util.Collections.addAll(list, buildEndpoints(ADMIN_ONLY_RESOURCES, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE));
    return list.toArray(new Endpoint[0]);
  }
}