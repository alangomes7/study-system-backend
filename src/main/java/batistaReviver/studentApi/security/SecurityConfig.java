package batistaReviver.studentApi.security;

import batistaReviver.studentApi.service.UserAppDetailsService;
import batistaReviver.studentApi.util.Role;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    System.out.println(
        "***************** Executou o método securityFilterChain de SecurityFilterChain");
    httpSecurity
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Cross Site Request Forgery - Um tipo de ataque utilizado em session based autentication
        // Em aplicações restful, como este tipo de ataque não acontece, deve ser desabilitado por
        // questão
        // de desempenho. Na linha abaixo é possível mudar para method reference.
        .csrf(c -> c.disable())
        .cors(c -> c.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.GET, "/produtos/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/produtos/**")
                    .hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, "/produtos/**")
                    .hasRole(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.DELETE, "/produtos/**")
                    .hasRole(Role.ADMIN.name())

                    // qq usuário pode cadastrar um usuário
                    .requestMatchers(HttpMethod.POST, "/usuarios")
                    .permitAll()

                    // qq usuário pode se logar
                    .requestMatchers(HttpMethod.POST, "/autenticacao/login")
                    .permitAll()

                    // Para acessar /favoritos é preciso estar logado
                    // .requestMatchers(HttpMethod.GET,"/favoritos/**").authenticated()
                    // .requestMatchers(HttpMethod.POST,"/favoritos/**").authenticated()
                    // .requestMatchers(HttpMethod.DELETE,"/favoritos/**").authenticated())

                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            c -> {
              // Quando o usuário não está logado, por default, é retornado o erro 403 - FORBIDDEN
              // Estamos mudando para 401 - UNAUTHORIZED
              c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
              // Quando o usuário está autenticado mas não possui o perfil (ROLE) necessário para
              // acessar o recurso, por default é retornado o erro 401 - UNAUTHORIZED.
              // Estamos mudando para 403 - FORBIDDEN
              c.accessDeniedHandler(
                  (request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                  });
            });

    return httpSecurity.build(); // Cria um objeto SecurityFilterChain.
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    System.out.println("1. ***** Executou o método passwordEncoder()");
    // BCryptPasswordEncoder é o algoritmo recomendado para efetuar o hash das senhas.
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    System.out.println("3. ***** Executou authenticationProvider()");
    var provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(userAppDetailsService);
    return provider;
  }

  // Quando tentarmos efetuar um login através de AuthenticationController, o método
  // authenticate() de AuthenticationManager vai chamar o método authenticate() de um
  // AuthenticationProvider (no nosso caso, DaoAuthenticationProvider), que irá chamar
  // o método loadUserByUsename() de UsuarioService (a classe que implementa a interface
  // UserDetailsService - que possui o método loadUserByUsername()).

  // O DaoAuthenticationProvider irá chamar o método loadUserByUsername() de
  // usuarioService para recuperar do banco de dados a senha do usuário. Em seguida ele
  // usa o passwordEncoder para criptografar a senha recebida do usuário e a compra com
  // a senha recuperada do banco de dados.
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    System.out.println("2. ***** Executou authenticationManager()");
    return config.getAuthenticationManager();
  }
}
