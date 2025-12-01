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

/**
 * Custom handler for access denied errors (Authorization).
 * <p>
 * This class is triggered when an <em>authenticated</em> user attempts to access
 * a resource they do not have the required role for (resulting in a 403 Forbidden error).
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles an access denied failure.
     * <p>
     * Writes a JSON {@link ErrorResponseApp} to the response stream indicating
     * that the user does not have permission.
     *
     * @param request               The request that resulted in an AccessDeniedException.
     * @param response              The response so that the user agent can be advised of the failure.
     * @param accessDeniedException The exception that caused the invocation.
     * @throws IOException If an input or output exception occurs.
     */
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