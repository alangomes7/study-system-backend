package batistaReviver.studentApi.dto;

import batistaReviver.studentApi.util.Permission;
import org.springframework.http.HttpMethod;

public record RouteRule(HttpMethod method, String pattern, Permission permission) {}
