package batistaReviver.studentApi.security;

import batistaReviver.studentApi.service.UserAppService;
import batistaReviver.studentApi.util.Role;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Main security configuration class for the application.
 * <p>
 * This class configures the Spring Security filter chain, defining:
 * <ul>
 * <li>CORS policies</li>
 * <li>CSRF settings (disabled for REST APIs)</li>
 * <li>Session management (Stateless for JWT)</li>
 * <li>Endpoint authorization rules</li>
 * <li>Exception handling for authentication and access denial</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserAppService userAppService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomAuthenticationEntryPoint customAuthEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final PasswordEncoder passwordEncoder;

  /**
   * Configures the Security Filter Chain.
   *    * <p>
   * This method defines the specific HTTP security rules. It ensures that the
   * {@link JwtAuthenticationFilter} is executed <em>before</em> the standard
   * {@link UsernamePasswordAuthenticationFilter}, allowing token-based authentication.
   *
   * @param http The {@link HttpSecurity} object to configure.
   * @return The built {@link SecurityFilterChain}.
   * @throws Exception if an error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> {
              // Public Endpoints
              for (EndpointConfig.Endpoint ep : EndpointConfig.PUBLIC_ENDPOINTS) {
                authorize.requestMatchers(ep.method(), ep.pattern()).permitAll();
              }
              // User or Admin Endpoints
              for (EndpointConfig.Endpoint ep : EndpointConfig.USER_OR_ADMIN_ENDPOINTS) {
                authorize.requestMatchers(ep.method(), ep.pattern())
                        .hasAnyRole(Role.USER.name(), Role.ADMIN.name());
              }
              // Admin Only Endpoints
              for (EndpointConfig.Endpoint ep : EndpointConfig.ADMIN_ENDPOINTS) {
                authorize.requestMatchers(ep.method(), ep.pattern()).hasRole(Role.ADMIN.name());
              }
              // Default to authenticated for anything else
              authorize.anyRequest().authenticated();
            })
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> {
              ex.authenticationEntryPoint(customAuthEntryPoint);
              ex.accessDeniedHandler(customAccessDeniedHandler);
            });

    return http.build();
  }

  /**
   * Configures the authentication provider.
   * <p>
   * Uses a {@link DaoAuthenticationProvider} to retrieve user details from the
   * database via {@link UserAppService} and verify passwords using the configured encoder.
   *
   * @return The configured {@link AuthenticationProvider}.
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userAppService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  /**
   * Exposes the {@link AuthenticationManager} bean.
   * <p>
   * This is required by the {@link batistaReviver.studentApi.controller.AuthenticationController}
   * to initiate the login process.
   *
   * @param config The {@link AuthenticationConfiguration}.
   * @return The {@link AuthenticationManager}.
   * @throws Exception If the manager cannot be created.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Configures Cross-Origin Resource Sharing (CORS).
   * <p>
   * Defines which origins (e.g., localhost:3000), methods, and headers are allowed.
   * It also exposes the 'Authorization' header so the frontend can read the token.
   *
   * @return The {@link CorsConfigurationSource}.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Strict allowed origins (Localhost typically used for dev)
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));

    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}