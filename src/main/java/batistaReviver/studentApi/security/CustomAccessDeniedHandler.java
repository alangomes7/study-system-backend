package batistaReviver.studentApi.security;

import batistaReviver.studentApi.exception.ErrorResponseApp;
import batistaReviver.studentApi.util.ObjectMapperApp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        ErrorResponseApp error = new ErrorResponseApp(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "FORBIDDEN",
                request.getMethod(),
                request.getRequestURI(),
                null,
                "Você não tem permissão para acessar este recurso." // accessDeniedException.getMessage()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            ObjectMapperApp.write(response, error);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
