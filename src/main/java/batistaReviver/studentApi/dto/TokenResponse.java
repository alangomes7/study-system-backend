package batistaReviver.studentApi.dto;

/**
 * Data Transfer Object (DTO) representing a successful authentication response.
 * <p>
 * This record is returned to the client after a valid login, containing the
 * JWT access token and essential user details required for the frontend UI.
 *
 * @param token  The JWT (JSON Web Token) access string used for subsequent authorized requests.
 * @param userId The unique identifier of the user in the database.
 * @param name   The display name of the user.
 * @param role   The user's role (e.g., USER, ADMIN), used for frontend permission handling.
 */
public record TokenResponse(String token, long userId, String name, String role) {}