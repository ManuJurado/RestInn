package RestInn.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    //@Value("${jwt.refreshExpiration}")
    //private long refreshExpiration;

    // Genera token de acceso (válido por 10 min)
    public String generateToken(String username) {
        return buildToken(username, expiration);
    }

    // Genera refresh token (válido por ejemplo 7 días)
    public String generateRefreshToken(String username) {
        return buildToken(username, expiration);
    }

    private String buildToken(String username, long expirationMillis) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Extraer el usuario del token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Verifica si el token es válido (firma y no expirado)
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}

