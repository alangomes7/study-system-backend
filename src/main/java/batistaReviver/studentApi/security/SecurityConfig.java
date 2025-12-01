package batistaReviver.studentApi.security;

import batistaReviver.studentApi.service.UserAppService;
import batistaReviver.studentApi.util.Role;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Main security configuration for the Student API.
 * <p>
 * This class configures the Spring Security filter chain, handling:
 * <ul>
 * <li>CORS (Cross-Origin Resource Sharing) policies</li>
 * <li>Stateless session management (suitable for REST APIs using JWT)</li>
 * <li>Endpoint authorization based on Roles (USER, ADMIN)</li>
 * <li>Integration of the JWT authentication filter</li>
 * <li>Custom exception handling for authentication and access denial</li>
 * </ul>
 */
@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserAppService userAppService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomAuthenticationEntryPoint customAuthEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  // --------------------------------------------------------------------------------
  // Security Filter Chain Configuration
  // --------------------------------------------------------------------------------

  /**
   * Defines the security filter chain that intercepts HTTP requests.
   *
   * <p>
   * This configuration:
   * 1. Disables CSRF (not needed for stateless JWT APIs).
   * 2. Sets session policy to STATELESS.
   * 3. Configures dynamic endpoint permissions via {@link EndpointConfig}.
   * 4. Adds the {@link JwtAuthenticationFilter} before the standard username/password filter.
   *
   * @param httpSecurity the HttpSecurity object to configure.
   * @return the built SecurityFilterChain.
   * @throws Exception if an error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            // Disable CSRF as we are using stateless JWT tokens
            .csrf(AbstractHttpConfigurer::disable)

            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Ensure no session is created (Stateless architecture)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Define URL authorization rules
            .authorizeHttpRequests(authorize -> {
              // 1. Public Endpoints
              for (EndpointConfig.Endpoint ep : EndpointConfig.PUBLIC_ENDPOINTS) {
                authorize.requestMatchers(ep.method(), ep.pattern()).permitAll();
              }

              // 2. User or Admin Endpoints
              for (EndpointConfig.Endpoint ep : EndpointConfig.USER_OR_ADMIN_ENDPOINTS) {
                authorize.requestMatchers(ep.method(), ep.pattern())
                        .hasAnyRole(Role.USER.name(), Role.ADMIN.name());
              }

              // 3. Admin Only Endpoints
              for (EndpointConfig.Endpoint ep : EndpointConfig.ADMIN_ENDPOINTS) {
                authorize.requestMatchers(ep.method(), ep.pattern()).hasRole(Role.ADMIN.name());
              }

              // All other requests require authentication
              authorize.anyRequest().authenticated();
            })

            // Add JWT Filter before the standard authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // Custom Exception Handling
            .exceptionHandling(ex -> {
              ex.authenticationEntryPoint(customAuthEntryPoint);
              ex.accessDeniedHandler(customAccessDeniedHandler);
            });

    return httpSecurity.build();
  }

  // --------------------------------------------------------------------------------
  // Authentication Beans
  // --------------------------------------------------------------------------------

  /**
   * Configures the AuthenticationProvider.
   * <p>
   * Connects the {@link UserAppService} (to load users) with the
   * {@link PasswordEncoder} (to verify hashed passwords).
   *
   * @return the configured DaoAuthenticationProvider.
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    // Explicitly set the UserDetailsService implementation
    provider.setUserDetailsService(userAppService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  /**
   * Exposes the AuthenticationManager as a Bean.
   * <p>
   * This is required to manually trigger authentication in the Login controller.
   *
   * @param config the AuthenticationConfiguration.
   * @return the AuthenticationManager.
   * @throws Exception if the manager cannot be retrieved.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
          throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Defines the password hashing algorithm.
   *
   * @return a BCryptPasswordEncoder instance.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // --------------------------------------------------------------------------------
  // CORS Configuration
  // --------------------------------------------------------------------------------

  /**
   * Configures Cross-Origin Resource Sharing (CORS) settings.
   * <p>
   * Defines which origins (e.g., frontend localhost), methods, and headers
   * are allowed to interact with this API.
   *
   * @return the CorsConfigurationSource.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allowed Origins (Consider moving hardcoded URLs to application.properties)
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));

    // Allowed HTTP Methods
    configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

    // Allowed Headers
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

    // Allow credentials (cookies, auth headers) to be sent
    configuration.setAllowCredentials(true);

    // Expose Authorization header to the client (useful for reading tokens)
    configuration.setExposedHeaders(List.of("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}