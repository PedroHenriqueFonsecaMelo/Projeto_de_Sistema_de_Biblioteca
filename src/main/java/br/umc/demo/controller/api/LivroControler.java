package br.umc.demo.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import br.umc.demo.entity.Livro;
import br.umc.demo.service.LivroService;

@RestController
@RequestMapping("/api/books")
@PreAuthorize("hasRole('LIBRARIAN')")
public class LivroControler {

    @Autowired
    private LivroService bookService;

    @PostMapping
    public ResponseEntity<Livro> create(@RequestBody Livro book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.cadastrarNovoMaterial(book));
    }

    @GetMapping
    public List<Livro> listAll() {
        return bookService.findAll();
    }

    @GetMapping("/search")
    public List<Livro> search(@RequestParam String query) {
        return bookService.searchBooks(query);
    }
}
