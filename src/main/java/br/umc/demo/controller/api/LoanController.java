package br.umc.demo.controller.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.umc.demo.dto.LoanRequest;
import br.umc.demo.entity.Loan;
import br.umc.demo.service.LoanService;

@RestController
@RequestMapping("/api/loans")
@PreAuthorize("hasRole('LIBRARIAN')")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/checkout")
    public ResponseEntity<Loan> checkout(@RequestBody LoanRequest request) {

        return ResponseEntity.ok(loanService.realizarEmprestimo(
            request.getLeitorId(), request.getBookId(), request.getBibliotecarioId()
        ));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Loan> processReturn(@PathVariable String id) {

        return ResponseEntity.ok(loanService.processarDevolucao(id));
    }


    @GetMapping
    public List<Map<String, Object>> getAllLoans() {

        return Arrays.asList(
            Map.of("id", "1", "book", "Java para Iniciantes", "member", "Pedro Melo", "dueDate", "2026-04-15", "status", "Ativo"),
            Map.of("id", "2", "book", "Clean Code", "member", "Guilherme", "dueDate", "2026-04-10", "status", "Atrasado")
        );
    }


    @PostMapping
    public Map<String, String> createLoan(@RequestBody Map<String, String> loanData) {
        System.out.println("Registrando empréstimo do livro: " + loanData.get("bookId"));
        return Map.of("message", "Empréstimo registrado com sucesso!");
    }


    @PutMapping("/{id}/return")
    public Map<String, String> returnBook(@PathVariable String id) {
        return Map.of("message", "Livro devolvido e disponível no acervo.");
    }
}
