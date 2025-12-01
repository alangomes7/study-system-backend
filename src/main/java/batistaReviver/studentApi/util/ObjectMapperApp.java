package batistaReviver.studentApi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ObjectMapperApp {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void write(HttpServletResponse response, Object value) throws IOException {
        mapper.writeValue(response.getOutputStream(), value);
    }
}
