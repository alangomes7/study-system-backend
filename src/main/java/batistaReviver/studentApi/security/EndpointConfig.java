package batistaReviver.studentApi.security;

import org.springframework.http.HttpMethod;

public class EndpointConfig {

  public static final Endpoint[] PUBLIC_ENDPOINTS = {
    new Endpoint(HttpMethod.GET, "/courses/**"),
          new Endpoint(HttpMethod.POST, "/authentication/**"),
  };

  public static final Endpoint[] USER_OR_ADMIN_ENDPOINTS = {
    new Endpoint(HttpMethod.GET, "/students/**"),
    new Endpoint(HttpMethod.GET, "/professors/**"),
    new Endpoint(HttpMethod.GET, "/study-classes/**"),
    new Endpoint(HttpMethod.GET, "/manage/**")
  };

  public static final Endpoint[] ADMIN_ENDPOINTS = {
          new Endpoint(HttpMethod.DELETE, "/students/**"),
          new Endpoint(HttpMethod.DELETE, "/professors/**"),
          new Endpoint(HttpMethod.DELETE, "/study-classes/**"),
          new Endpoint(HttpMethod.DELETE, "/manage/**")
  };

  public record Endpoint(HttpMethod method, String pattern) {}
}
