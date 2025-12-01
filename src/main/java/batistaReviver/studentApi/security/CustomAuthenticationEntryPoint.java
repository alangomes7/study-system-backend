package batistaReviver.studentApi.security;

import batistaReviver.studentApi.exception.ErrorResponseApp;
import batistaReviver.studentApi.util.ObjectMapperApp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

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
