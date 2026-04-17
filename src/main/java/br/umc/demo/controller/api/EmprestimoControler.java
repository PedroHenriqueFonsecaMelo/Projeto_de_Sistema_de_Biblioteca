package br.umc.demo.controller.api;

import br.umc.demo.entity.Emprestimo;
import br.umc.demo.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/emprestimos")
@PreAuthorize("hasRole('LIBRARIAN')")
@RequiredArgsConstructor
public class EmprestimoControler {

    private final EmprestimoService emprestimoService;

    // REALIZAR NOVO EMPRÉSTIMO
    @PostMapping
    public ResponseEntity<Emprestimo> realizarEmprestimo(@RequestBody Emprestimo emprestimo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emprestimoService.salvarEmprestimo(emprestimo));
    }

    // DEVOLVER LIVRO (Finalizar empréstimo)
    @PostMapping("/devolver/{id}")
    public ResponseEntity<Void> devolverLivro(@PathVariable String id) {
        emprestimoService.finalizarEmprestimo(id);
        return ResponseEntity.ok().build();
    }

    // RENOVAR EMPRÉSTIMO
    @PostMapping("/renovar/{id}")
    public ResponseEntity<Void> renovarEmprestimo(@PathVariable String id) {
        emprestimoService.renovar(id);
        return ResponseEntity.ok().build();
    }
}