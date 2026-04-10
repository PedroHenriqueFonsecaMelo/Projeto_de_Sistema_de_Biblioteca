package br.umc.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        try {
            String token = Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();
            logger.info("✓ JWT token gerado para user: {}", username);
            return token;
        } catch (Exception e) {
            logger.error("✗ Erro gerando JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return extractUsername(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            boolean isValid = username != null && 
                             username.equals(userDetails.getUsername()) && 
                             !isTokenExpired(token);
            
            if (!isValid) {
                logger.warn("✗ Token inválido para user: {} (username mismatch ou expired)", 
                    userDetails.getUsername());
            } else {
                logger.info("✓ Token válido para user: {}", userDetails.getUsername());
            }
            return isValid;
        } catch (Exception e) {
            logger.error("✗ Erro validando token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            if (expiration == null) {
                logger.warn("✗ Token sem data de expiração");
                return true;
            }
            boolean isExpired = expiration.before(new Date());
            if (isExpired) {
                logger.warn("✗ Token expirado em: {}", expiration);
            }
            return isExpired;
        } catch (Exception e) {
            logger.error("✗ Erro checando expiração do token: {}", e.getMessage());
            return true;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = getAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("✗ Erro extraindo claim do token: {}", e.getMessage());
            return null;
        }
    }

    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            logger.error("✗ Erro parsando JWT token: {}", e.getMessage());
            throw e;
        }
    }
}
