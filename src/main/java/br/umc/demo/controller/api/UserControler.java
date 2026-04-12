package br.umc.demo.controller.api;

import br.umc.demo.entity.User;
import br.umc.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserControler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@RequestParam String nome,
            @RequestParam String email) {

        User user = new User();
        user.setNome(nome);
        user.setEmail(email);
        user.setRoles(Collections.emptySet());
        user.setPassword(null);
        user.setAtivo(true);

        userRepository.save(user);

        return ResponseEntity.status(302).header("Location", "/controle").build();
    }

    @SuppressWarnings("null")
    @PostMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestParam String id,
            @RequestParam String endereco,
            @RequestParam String telefone) {

        userRepository.findById(id).ifPresent(user -> {
            user.setEndereco(endereco);
            user.setTelefone(telefone);
            userRepository.save(user);
        });

        return ResponseEntity.status(302).header("Location", "/controle").build();
    }

    @SuppressWarnings("null")
    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivateUser(@RequestParam String id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setAtivo(false);
            userRepository.save(user);
        });
        return ResponseEntity.status(302).header("Location", "/controle").build();
    }

    @SuppressWarnings("null")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam String id) {
        userRepository.deleteById(id);
        return ResponseEntity.status(302).header("Location", "/controle").build();
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAllAtivos();
    }
}
