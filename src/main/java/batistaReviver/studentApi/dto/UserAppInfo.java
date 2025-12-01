package batistaReviver.studentApi.dto;

/**
 * Data Transfer Object (DTO) for conveying user validation status.
 * <p>
 * This record is typically used to return the result of checks, such as
 * verifying if a user already exists during registration or data import processes.
 *
 * @param valid      Indicates if the user data is considered valid.
 * @param duplicated Indicates if the user already exists in the system.
 * @param msg        A descriptive message explaining the status.
 */
public record UserAppInfo(boolean valid, boolean duplicated, String msg) {}