package br.umc.demo.entity;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Document(collection = "users")
@Data
public class User implements UserDetails { // <-- Agora o Spring entende quem é você
    
    @Id
    private String id;
    private String nome;
    private String email;
    private String password;
    private String telefone;
    private String endereco;
    private String documentoIdentidade;
    

    private Set<String> roles;
    private boolean ativo = true;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email; // O e-mail é o login do seu sistema
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo; // Usa o seu campo 'ativo' para bloquear login se necessário
    }
}