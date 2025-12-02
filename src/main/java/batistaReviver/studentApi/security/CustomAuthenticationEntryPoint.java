package batistaReviver.studentApi.security;

import batistaReviver.studentApi.exception.ErrorResponseApp;
import batistaReviver.studentApi.util.ObjectMapperApp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Custom entry point for handling authentication errors.
 * <p>
 * This class is triggered when an unauthenticated user attempts to access a protected resource
 * (resulting in a 401 Unauthorized error). It customizes the response body to match the
 * application's standard error format.
 */
@Component
public class CustomAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     * <p>
     * Writes a JSON {@link ErrorResponseApp} to the response stream indicating
     * that authentication is required.
     *
     * @param request       The request that resulted in an AuthenticationException.
     * @param response      The response so that the user agent can begin authentication.
     * @param authException The exception that caused the invocation.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) {

        ErrorResponseApp error = new ErrorResponseApp(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                request.getMethod(),
                request.getRequestURI(),
                null,
                "Necessário estar autenticado para acessar este recurso." // authException.getMessage()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            ObjectMapperApp.write(response, error);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}