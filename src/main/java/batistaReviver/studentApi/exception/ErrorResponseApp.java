package batistaReviver.studentApi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseApp(
        LocalDateTime timestamp,
        int status,
        String error,
        String method,
        String path,
        Map<String, String> fieldErrors,
        String message
) {}
