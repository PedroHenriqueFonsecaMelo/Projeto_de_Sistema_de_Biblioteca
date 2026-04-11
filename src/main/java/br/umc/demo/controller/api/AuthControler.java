package br.umc.demo.controller.api;

import br.umc.demo.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/api/auth")
public class AuthControler {

    private static final Logger logger = LoggerFactory.getLogger(AuthControler.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    /*
     * @PostMapping("/login")
     * public String login(@RequestParam String username, @RequestParam String
     * password, HttpServletResponse response) {
     * try {
     * logger.info("Tentativa de login para o usuário: {}", username);
     *
     * 
     * Authentication auth = authenticationManager.authenticate(
     * new UsernamePasswordAuthenticationToken(username, password));
     *
     * 
     * String token = jwtService.generateToken(auth.getName());
     *
     * 
     * Cookie cookie = new Cookie("AUTH-TOKEN", token);
     * cookie.setHttpOnly(true);
     * cookie.setSecure(false);
     * cookie.setPath("/");
     * cookie.setMaxAge(86400);
     *
     * 
     * o Chrome
     * response.setHeader("Set-Cookie",
     * String.format("%s=%s; Max-Age=%d; Path=%s; HttpOnly; SameSite=Lax",
     * cookie.getName(), cookie.getValue(), cookie.getMaxAge(), cookie.getPath()));
     *
     * logger.info("Login bem-sucedido. Redirecionando para /dashboard...");
     * return "redirect:/dashboard";
     *
     * } catch (Exception e) {
     * logger.error("Erro na autenticação: {}", e.getMessage());
     * return "redirect:/?error=true";
     * }
     * }
     *
     * @PostMapping("/logout")
     * public String logout(HttpServletResponse response) {
     * 
     * Cookie cookie = new Cookie("AUTH-TOKEN", null);
     * cookie.setHttpOnly(true);
     * cookie.setSecure(false);
     * cookie.setPath("/");
     * cookie.setMaxAge(0);
     * response.addCookie(cookie);
     *
     * logger.info("Logout realizado. Redirecionando para a página inicial...");
     * return "redirect:/";
     * }
     */

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        try {
            logger.info("========================================");
            logger.info("🔐 Tentativa de LOGIN para user: {}", username);
            logger.info("========================================");

            // Authenticate user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            logger.info("✓ Autenticação bem-sucedida para: {}", auth.getName());
            logger.info("✓ Autoridades do user: {}", auth.getAuthorities());

            // Generate JWT token
            String token = jwtService.generateToken(auth.getName());
            logger.info("✓ Token JWT gerado com sucesso");

            // Create and set cookie
            Cookie cookie = new Cookie("AUTH-TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);

            logger.info("✓ Cookie AUTH-TOKEN setado com sucesso");
            logger.info("========================================");
            logger.info("✓ Redirecionando para /dashboard...");
            logger.info("========================================");

            return "redirect:/library/dashboard";

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("✗ ERRO NA AUTENTICAÇÃO: {}", e.getMessage());
            logger.error("========================================", e);
            return "redirect:/?error=true";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("AUTH-TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        logger.info("Logout realizado.");
        return "redirect:/";
    }

}
