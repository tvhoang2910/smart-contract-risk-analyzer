package vn.techmaster.nowj.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT secret key for token signing and verification.
     * Should be at least 32 characters for HS256 algorithm.
     */
    private String secret;

    /**
     * JWT token expiration time in milliseconds.
     * Default: 86400000 ms (24 hours)
     */
    private int expirationMs = 86400000;
}
