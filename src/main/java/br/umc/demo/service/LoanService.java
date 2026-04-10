package br.umc.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.umc.demo.entity.Loan;
import br.umc.demo.entity.Book;
import br.umc.demo.entity.LoanStatus;
import br.umc.demo.repository.BookRepository;
import br.umc.demo.repository.LoanRepository;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;

    public Loan realizarEmprestimo(String leitorId, String bookId, String bibliotecarioId) {
        Loan loan = new Loan();
        loan.setLeitorId(leitorId);
        loan.setBibliotecarioId(bibliotecarioId);
        loan.setBookId(bookId);
        loan.setDataEmprestimo(LocalDateTime.now());
        loan.setDataVencimento(LocalDateTime.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);

        @SuppressWarnings("null")
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() - 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    /**
     * Unified method for loan finalization/return.
     * Merges logic from processarDevolucao and finalizarEmprestimo.
     * Updates status, dates, book availability, and new fields.
     */
    @SuppressWarnings("null")
    public Loan finalizarEmprestimo(String id) {
        // 1. Busca o documento de empréstimo
        Loan emprestimo = loanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        // 2. Lógica de Negócio: Finaliza empréstimo
        emprestimo.setStatus(LoanStatus.RETURNED);
        emprestimo.setDataDevolucao(LocalDateTime.now());
        emprestimo.setAtivo(false);
        emprestimo.setDataDevolucaoReal(LocalDate.now());

        // 3. Atualiza o Livro: incrementa exemplares disponíveis
        bookRepository.findById(emprestimo.getBookId()).ifPresent(book -> {
            book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() + 1);
            bookRepository.save(book);
        });

        // 4. Salva a alteração do empréstimo
        return loanRepository.save(emprestimo);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
