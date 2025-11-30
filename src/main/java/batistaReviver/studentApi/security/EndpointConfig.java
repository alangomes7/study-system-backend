package batistaReviver.studentApi.security;

import org.springframework.http.HttpMethod;

public class EndpointConfig {

  public static final Endpoint[] PUBLIC_ENDPOINTS = {
    new Endpoint(HttpMethod.GET, "/courses/**"),
          new Endpoint(HttpMethod.POST, "/authentication/**"),
  };

  public static final Endpoint[] USER_OR_ADMIN_ENDPOINTS = {
    new Endpoint(HttpMethod.POST, "/students/**"),
    new Endpoint(HttpMethod.POST, "/professors/**"),
    new Endpoint(HttpMethod.DELETE, "/study-classes/**"),
    new Endpoint(HttpMethod.POST, "/manage/**")
  };

  public static final Endpoint[] ADMIN_ENDPOINTS = {
    new Endpoint(HttpMethod.PUT, "/manage/**"),
  };

  public record Endpoint(HttpMethod method, String pattern) {}
}
