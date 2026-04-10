package br.umc.demo.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public ResponseEntity<Void> checkout(
            @RequestParam String leitorId,
            @RequestParam String bookId,
            @RequestParam(required = false) String bibliotecarioId) {

        loanService.realizarEmprestimo(leitorId, bookId, bibliotecarioId);

        return ResponseEntity.status(302)
                .header("Location", "/livros/emprestimos")
                .build();
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Loan> processReturn(@PathVariable String id) {

        return ResponseEntity.ok(loanService.finalizarEmprestimo(id));
    }

    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
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

    @PostMapping("/devolver/{id}")
    public ResponseEntity<Void> devolverLivro(@PathVariable("id") String id) {
        try {
            loanService.finalizarEmprestimo(id);

            return ResponseEntity.status(302)
                    .header("Location", "/livros/emprestimos")
                    .build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
