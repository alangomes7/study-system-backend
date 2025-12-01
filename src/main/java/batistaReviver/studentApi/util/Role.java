package batistaReviver.studentApi.util;

/**
 * Enumeration of available user roles in the application.
 * <p>
 * These roles are used to define authority levels for authorization checks
 * within the {@link batistaReviver.studentApi.security.SecurityConfig} and
 * JWT token generation.
 */
public enum Role {
  /**
   * Represents an administrator with elevated privileges (e.g., DELETE operations).
   */
  ADMIN,

  /**
   * Represents a standard user with basic read/write access to their own data.
   */
  USER
}