package br.umc.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String username;
    private List<String> authorities;

    public JwtResponse(String token, String username, List<String> authorities) {
        this.token = token;
        this.username = username;
        this.authorities = authorities;
    }
}
