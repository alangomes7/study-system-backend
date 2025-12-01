package batistaReviver.studentApi.util;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Data Transfer Object (DTO) for user authentication.
 * <p>
 * This class captures the credentials sent by the client during the login process.
 * It includes Jakarta Validation annotations to ensure data integrity before
 * processing.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAppLogin {

  /**
   * The user's email address.
   * <p>
   * Must not be blank and must adhere to standard email formatting.
   */
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  /**
   * The user's password.
   * <p>
   * This is the raw password provided by the user, which will be compared
   * against the encrypted password in the database.
   */
  @NotBlank(message = "Password is required")
  private String password;
}