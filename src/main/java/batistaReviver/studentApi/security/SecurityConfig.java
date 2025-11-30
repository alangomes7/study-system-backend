package batistaReviver.studentApi.security;

import batistaReviver.studentApi.service.UserAppDetailsService;
import batistaReviver.studentApi.util.Role;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserAppDetailsService userAppDetailsService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            authorize -> {
              // Public
              for (EndpointConfig.Endpoint ep : EndpointConfig.PUBLIC_ENDPOINTS) {
                authorize.requestMatchers(ep.method(), ep.pattern()).permitAll();
              }

              // User or Admin
              for (EndpointConfig.Endpoint ep : EndpointConfig.USER_OR_ADMIN_ENDPOINTS) {
                authorize
                    .requestMatchers(ep.method(), ep.pattern())
                    .hasAnyRole(Role.USER.name(), Role.ADMIN.name());
              }

              // Admin only
              for (EndpointConfig.Endpoint ep : EndpointConfig.ADMIN_ENDPOINTS) {
                authorize.requestMatchers(ep.method(), ep.pattern()).hasRole(Role.ADMIN.name());
              }

              authorize.anyRequest().authenticated();
            })
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            exceptions -> {
              exceptions.authenticationEntryPoint(
                  new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
              exceptions.accessDeniedHandler(
                  (request, response, accessDeniedException) ->
                      response.setStatus(HttpStatus.FORBIDDEN.value()));
            });

    return httpSecurity.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userAppDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allowed Origins
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));

    // Allowed Methods
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

    // Allowed Headers
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

    // Allow credentials (cookies, auth headers)
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(List.of("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
