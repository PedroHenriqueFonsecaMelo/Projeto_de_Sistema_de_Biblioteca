package br.umc.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(@SuppressWarnings("null") HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = extractTokenFromRequest(request);
            
            if (jwt != null) {
                logger.debug("Processing JWT token for request: {}", request.getRequestURI());
                authenticateWithToken(jwt, request);
            } else {
                logger.debug("No JWT token found in request: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            logger.error("Error in JWT filter: {}", e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // Try to get token from cookies first
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("AUTH-TOKEN".equals(cookie.getName())) {
                    logger.debug("JWT token found in cookie");
                    return cookie.getValue();
                }
            }
        }

        // Try to get token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.debug("JWT token found in Authorization header");
            return authHeader.substring(7);
        }

        return null;
    }

    private void authenticateWithToken(String jwt, HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.debug("Authentication already set, skipping JWT processing");
            return;
        }

        try {
            String userEmail = jwtService.extractUsername(jwt);

            if (userEmail == null) {
                logger.warn("Could not extract username from JWT token");
                return;
            }

            logger.debug("Extracted username from token: {}", userEmail);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                logger.info("✓ JWT token válido para user: {}", userEmail);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("✓ User autenticado com sucesso: {} usando JWT", userEmail);
            } else {
                logger.warn("✗ Validação do JWT token falhou para user: {}", userEmail);
            }
        } catch (Exception e) {
            logger.error("✗ Erro processando JWT token: {}", e.getMessage(), e);
        }
    }
}