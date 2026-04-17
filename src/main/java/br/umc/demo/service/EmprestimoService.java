package br.umc.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.umc.demo.entity.Emprestimo;
import br.umc.demo.entity.Livro;
import br.umc.demo.entity.enums.EmprestimoStatus;
import br.umc.demo.repository.LivroRepository;
import br.umc.demo.repository.EmprestimoRepository;

@Service
public class EmprestimoService {
    @Autowired
    private EmprestimoRepository loanRepository;
    @Autowired
    private LivroRepository bookRepository;

    public Emprestimo realizarEmprestimo(String leitorId, String bookId, String bibliotecarioId) {
        Emprestimo loan = new Emprestimo();
        loan.setLeitorId(leitorId);
        loan.setBibliotecarioId(bibliotecarioId);
        loan.setBookId(bookId);
        loan.setDataEmprestimo(LocalDateTime.now());
        loan.setDataVencimento(LocalDateTime.now().plusDays(14));
        loan.setStatus(EmprestimoStatus.ATIVO);

        @SuppressWarnings("null")
        Optional<Livro> optBookCheck = bookRepository.findById(bookId);
        if (!optBookCheck.isPresent()) {
            throw new RuntimeException("Livro não encontrado para empréstimo");
        }
        Livro book = optBookCheck.get();
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
    public Emprestimo finalizarEmprestimo(String id) {
        // 1. Busca o documento de empréstimo
        Optional<Emprestimo> optEmprestimo = loanRepository.findById(id);
        if (!optEmprestimo.isPresent()) {
            throw new RuntimeException("Empréstimo não encontrado");
        }
        Emprestimo emprestimo = optEmprestimo.get();

        // 2. Lógica de Negócio: Finaliza empréstimo
        emprestimo.setStatus(EmprestimoStatus.RETORNADO);
        emprestimo.setDataDevolucao(LocalDateTime.now());
        emprestimo.setAtivo(false);
        emprestimo.setDataDevolucaoReal(LocalDate.now());

        // 3. Atualiza o Livro: incrementa exemplares disponíveis
        Optional<Livro> optBook = bookRepository.findById(emprestimo.getBookId());
        if (optBook.isPresent()) {
            Livro book = optBook.get();
            book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() + 1);
            bookRepository.save(book);
        }

        // 4. Salva a alteração do empréstimo
        return loanRepository.save(emprestimo);
    }

    public List<Emprestimo> getAllLoans() {
        return loanRepository.findAll();
    }
}
