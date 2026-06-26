package gde.gde_website.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Utility component for creating, validating and parsing JSON Web Tokens (JWT)
 * <p>This class uses a secret key loaded from application properties to sign and verify tokens. It also
 * provides helper methods for extracting claims such as the user ID</p>
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Builds the HMAC secret key from the configured secret string
     * @return secret key userd for JWT signing and verification
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Parses the token and returns all JWT claims if the token is valid
     * @param token JWT string
     * @return all claims stored in the token payload
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the user ID from the custom JWT claim named {@code userId}
     * @param token JWT string
     * @return user ID stored inside the token
     */
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    /**
     * Checks whether the token is valid by attempting to parse it.
     * @param token JWT string
     * @return {@code true} if the token can be parsed and verified; otherwise {@code false}
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a signed JWT token with the specified user ID stored as a custom claim.
     * @param userId user  identifier to embed into the token
     * @return signed JWT string
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3 * 60 * 60 * 1000L);

        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getKey())
                .compact();
    }
}