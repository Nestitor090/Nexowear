package com.nexowear.Nexowear.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Jalamos la clave secreta que configuramos en el application.properties
    @Value("${nexowear.jwt.secret}")
    private String jwtSecret;

    // Jalamos el tiempo de expiración
    @Value("${nexowear.jwt.expiration}")
    private int jwtExpirationMs;

    // Convierte la clave en texto plano a un objeto Key criptográfico seguro
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 1. GENERAR EL TOKEN (Se ejecuta cuando el usuario hace login con éxito)
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Guardamos el username dentro del token
                .setIssuedAt(new Date()) // Fecha de creación
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Fecha de expiración (24h)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firmamos criptográficamente
                .compact();
    }

    // 2. EXTRAER EL USERNAME (Para saber a quién le pertenece el token en cada petición)
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3. VALIDAR EL TOKEN (Revisa que no esté alterado, falsificado o vencido)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Token JWT inválido: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("El token JWT ha expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token JWT no soportado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("La cadena claims de JWT está vacía: " + e.getMessage());
        }
        return false;
    }
}