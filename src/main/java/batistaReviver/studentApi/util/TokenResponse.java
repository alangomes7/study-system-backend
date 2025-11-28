package batistaReviver.studentApi.util;

public record TokenResponse(String token, long userId, String name, String role) {}
