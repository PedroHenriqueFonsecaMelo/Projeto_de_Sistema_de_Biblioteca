package br.umc.demo.controller.api;

import br.umc.demo.entity.User;
import br.umc.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("null")
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveUser(@RequestBody User user) {
        try {
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Usuário salvo!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
