package batistaReviver.studentApi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Utility class for handling JSON serialization manually.
 * <p>
 * This class provides a pre-configured {@link ObjectMapper} instance. It is primarily
 * used in low-level components like Security Filters or Exception Handlers where
 * Spring MVC's automatic serialization (via {@code @ResponseBody}) is not available.
 */
public class ObjectMapperApp {

    /**
     * A shared {@link ObjectMapper} instance configured with the {@link JavaTimeModule}.
     * <p>
     * The {@code JavaTimeModule} is essential for correctly serializing Java 8
     * {@code LocalDateTime} objects into JSON strings.
     */
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * Serializes an object to JSON and writes it directly to the HTTP response stream.
     *
     * @param response The {@link HttpServletResponse} object to write to.
     * @param value    The object to serialize (e.g., an ErrorResponseApp).
     * @throws IOException If an input or output exception occurs during writing.
     */
    public static void write(HttpServletResponse response, Object value) throws IOException {
        mapper.writeValue(response.getOutputStream(), value);
    }
}