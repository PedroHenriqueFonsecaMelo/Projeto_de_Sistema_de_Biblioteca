package br.umc.demo.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import br.umc.demo.entity.Book;
import br.umc.demo.service.BookService;

@RestController
@RequestMapping("/api/books")
@PreAuthorize("hasRole('LIBRARIAN')")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.cadastrarNovoMaterial(book));
    }

    @GetMapping
    public List<Book> listAll() {
        return bookService.findAll();
    }

    @GetMapping("/search")
    public List<Book> search(@RequestParam String query) {
        return bookService.searchBooks(query);
    }
}
