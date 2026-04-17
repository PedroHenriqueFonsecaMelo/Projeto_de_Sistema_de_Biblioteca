package br.umc.demo.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import br.umc.demo.entity.Livro;
import br.umc.demo.service.LivroService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/livros")
@PreAuthorize("hasRole('LIBRARIAN')")
@RequiredArgsConstructor
public class LivroControler {

    private final LivroService bookService;

    @GetMapping
    public List<Livro> listAll() {
        return bookService.findAll();
    }

    @GetMapping("/search")
    public List<Livro> search(@RequestParam String query) {
        return bookService.searchBooks(query);
    }

    @PostMapping
    public ResponseEntity<Livro> create(@RequestBody Livro book) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.cadastrarNovoMaterial(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookService.excluirMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/isbn/{isbn}")
    public ResponseEntity<Void> deleteByISBN(@PathVariable String isbn) {
        bookService.deleteByIsbn(isbn);
        return ResponseEntity.noContent().build();
    }
}