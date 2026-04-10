package br.umc.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.umc.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                br.umc.demo.entity.User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .authorities(user.getRoles().stream()
                                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                                .toList())
                                .build();
        }
}
