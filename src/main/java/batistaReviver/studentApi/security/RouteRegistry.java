package batistaReviver.studentApi.security;

import batistaReviver.studentApi.dto.RouteRule;
import batistaReviver.studentApi.util.Permission;
import org.springframework.http.HttpMethod;

public class RouteRegistry {

    public static final RouteRule[] RULES = {

            // PUBLIC
            new RouteRule(HttpMethod.GET,  "/courses/**",          Permission.PUBLIC),
            new RouteRule(HttpMethod.POST, "/authentication/**",   Permission.PUBLIC),

            // USER or ADMIN
            new RouteRule(HttpMethod.GET,  "/students/**",         Permission.USER),
            new RouteRule(HttpMethod.POST, "/students/**",         Permission.USER),

            new RouteRule(HttpMethod.GET,  "/professors/**",       Permission.USER),
            new RouteRule(HttpMethod.POST, "/professors/**",       Permission.USER),

            new RouteRule(HttpMethod.GET,  "/study-classes/**",    Permission.USER),
            new RouteRule(HttpMethod.POST, "/study-classes/**",    Permission.USER),

            new RouteRule(HttpMethod.GET,  "/manage/**",           Permission.USER),
            new RouteRule(HttpMethod.POST, "/manage/**",           Permission.USER),

            new RouteRule(HttpMethod.GET,    "/subscriptions/**",  Permission.USER),
            new RouteRule(HttpMethod.POST,   "/subscriptions/**",  Permission.USER),

            // ADMIN only for UPDATE & DELETE in core resources
            new RouteRule(HttpMethod.PUT,    "/students/**",       Permission.ADMIN),
            new RouteRule(HttpMethod.DELETE, "/students/**",       Permission.ADMIN),

            new RouteRule(HttpMethod.PUT,    "/professors/**",     Permission.ADMIN),
            new RouteRule(HttpMethod.DELETE, "/professors/**",     Permission.ADMIN),

            new RouteRule(HttpMethod.PUT,    "/study-classes/**",  Permission.ADMIN),
            new RouteRule(HttpMethod.DELETE, "/study-classes/**",  Permission.ADMIN),

            new RouteRule(HttpMethod.PUT,    "/manage/**",         Permission.ADMIN),
            new RouteRule(HttpMethod.DELETE, "/manage/**",         Permission.ADMIN),

            // ADMIN ONLY – subscription operations
            new RouteRule(HttpMethod.PUT,    "/subscriptions/**",  Permission.ADMIN),
            new RouteRule(HttpMethod.DELETE, "/subscriptions/**",  Permission.ADMIN)
    };
}
