package br.umc.demo.controller.api;

import br.umc.demo.entity.Usuario;
import br.umc.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsuarioControler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@RequestParam String nome,
            @RequestParam String email) {

        Usuario user = new Usuario();
        user.setNome(nome);
        user.setEmail(email);
        user.setRoles(Collections.emptySet());
        user.setPassword(null);
        user.setAtivo(true);

        userRepository.save(user);

        return ResponseEntity.status(302).header("Location", "/library/controle").build();
    }

    @SuppressWarnings("null")
    @PostMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestParam String id,
            @RequestParam String endereco,
            @RequestParam String telefone) {

        Optional<Usuario> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            Usuario user = optUser.get();
            user.setEndereco(endereco);
            user.setTelefone(telefone);
            userRepository.save(user);
        }

        return ResponseEntity.status(302).header("Location", "/library/controle").build();
    }

    @SuppressWarnings("null")
    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivateUser(@RequestParam String id) {
        Optional<Usuario> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            Usuario user = optUser.get();
            user.setAtivo(false);
            userRepository.save(user);
        }
        return ResponseEntity.status(302).header("Location", "/library/controle").build();
    }

    @SuppressWarnings("null")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam String id) {
        userRepository.deleteById(id);
        return ResponseEntity.status(302).header("Location", "/library/controle").build();
    }

    @GetMapping
    public List<Usuario> getAllUsers() {
        return userRepository.findAllAtivos();
    }
}
