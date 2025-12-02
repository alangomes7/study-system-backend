package batistaReviver.studentApi.security;

import batistaReviver.studentApi.exception.JwtAuthenticationException;
import batistaReviver.studentApi.service.JwtService;
import batistaReviver.studentApi.util.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Filter that executes once per request to validate JWT tokens.
 * <p>
 * This filter intercepts HTTP requests to check for the "Authorization" header.
 * If a valid Bearer token is present, it extracts user details and sets the
 * authentication in the {@link SecurityContextHolder}, effectively logging the user in
 * for the duration of the request.
 */
@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  /**
   * Performs the filtering logic.
   * <ol>
   * <li>Checks if the Authorization header exists and starts with "Bearer ".</li>
   * <li>Validates the token using {@link JwtService}.</li>
   * <li>Extracts user ID and Role.</li>
   * <li>Creates an {@link UsernamePasswordAuthenticationToken} and sets it in the context.</li>
   * </ol>
   *
   * @param request     The incoming HTTP request.
   * @param response    The outgoing HTTP response.
   * @param filterChain The chain of filters to proceed with.
   * @throws ServletException If a servlet error occurs.
   * @throws IOException      If an I/O error occurs.
   */
  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    // No token → continue normally (public endpoints)
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7); // after "Bearer "

    try {
      // Let JwtService throw detailed exceptions
      jwtService.validateOrThrow(token);

      long userId = jwtService.getUserIdFromToken(token);
      Role role = jwtService.getRoleFromToken(token);

      var authenticationToken = new UsernamePasswordAuthenticationToken(
              userId,
              null,
              List.of(new SimpleGrantedAuthority("ROLE_" + role))
      );

      authenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authenticationToken);

      filterChain.doFilter(request, response);
      System.out.println(" - - - - - - - - - - -");
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm:ss:SSS");
      String formatted = LocalDateTime.now().format(formatter);
      System.out.println("Timestamp: " + formatted);
      System.out.println("User: " + userId);
      System.out.println("Role: " + role);
      System.out.println("Authentication: " + authenticationToken.getDetails());
      System.out.println("SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());
      System.out.println("Response: " + response.getStatus() + " | " + response.getContentType());
      System.out.println(" - - - - - - - - - - -");
    } catch (JwtAuthenticationException ex) {
      // Send the error to the AuthenticationEntryPoint
      // Spring Security will call CustomAuthenticationEntryPoint
      SecurityContextHolder.clearContext();
      request.setAttribute("jwt_exception", ex);
      throw ex;
    }
  }
}