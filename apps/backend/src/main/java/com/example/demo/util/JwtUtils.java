package com.example.demo.util;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.model.AppUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.security.jwt-config.secret-key}")
    private String secretKey;

    public String generateToken(String subject) {
        Map<String, String> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (int)(1000 * 60 * 60 * 0.5))) // 1/2 hour
                .signWith(getKey())
                .compact();
    }

    public String generateTokenFromUserDetails(AppUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("username", userDetails.getUsername());
        claims.put("email", userDetails.getEmail());
        if( userDetails.getAuthorities() != null ) {
            String roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            claims.put("roles", roles);
        }
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (int)(1000 * 60 * 60 * 0.5))) // 1/2 hour
                .signWith(getKey())
                .compact();
    }

    public String getJwtTokenFromHeader(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public String extractUserEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is not supported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public boolean validateToken(String token, String email) {
        return (email.equals(extractUserEmail(token)) && !isTokenExpired(token));
    }
}
