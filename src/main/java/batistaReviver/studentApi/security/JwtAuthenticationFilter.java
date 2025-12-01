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
import java.util.List;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

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

    } catch (JwtAuthenticationException ex) {
      // Send the error to the AuthenticationEntryPoint
      // Spring Security will call CustomAuthenticationEntryPoint
      SecurityContextHolder.clearContext();
      request.setAttribute("jwt_exception", ex);
      throw ex;
    }
  }
}
