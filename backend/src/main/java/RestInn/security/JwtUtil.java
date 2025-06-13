package RestInn.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expiration;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generar access token con rol incluido
    public String generateAccessToken(String username, String role) {
        return buildToken(username, role, expiration, "access");
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, null, refreshExpiration, "refresh");
    }

    public Claims claims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isValid(String token, String username) {
        try {
            Claims c = claims(token);
            return c.getSubject().equals(username)
                    && c.getExpiration().after(new Date())
                    && "access".equals(c.get("type"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String buildToken(String user, String role, long ttl, String type) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .setSubject(user)
                .claim("type", type)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ttl))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256);

        if (role != null) {
            builder.claim("role", "ROLE_" + role);
        }

        return builder.compact();
    }

    public boolean isValidRefreshToken(String token, String username) {
        try {
            Claims c = claims(token);
            return c.getSubject().equals(username)
                    && c.getExpiration().after(new Date())
                    && "refresh".equals(c.get("type"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
