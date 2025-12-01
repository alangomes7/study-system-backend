package batistaReviver.studentApi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * General application configuration beans.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Provides the password encoder bean.
     * <p>
     * Returns a {@link BCryptPasswordEncoder}, which is the standard strong hashing function
     * used for storing user passwords securely.
     *
     * @return The {@link PasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}