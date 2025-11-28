package batistaReviver.studentApi.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponseApp(
    LocalDateTime localDateTime,
    int errorCode,
    String error,
    String method,
    String requestUri,
    Map<String, String> map,
    String message) {}
