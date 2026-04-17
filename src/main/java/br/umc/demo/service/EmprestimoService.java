package br.umc.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @SuppressWarnings("null")
    @Transactional
    public Emprestimo salvarEmprestimo(Emprestimo emprestimo) {
        Livro book = bookRepository.findById(emprestimo.getBookId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        if (book.getExemplaresDisponiveis() <= 0) {
            throw new RuntimeException("Não há exemplares disponíveis para este livro.");
        }

        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataVencimento(LocalDateTime.now().plusDays(14));
        emprestimo.setStatus(EmprestimoStatus.ATIVO);
        emprestimo.setAtivo(true);

        // Atualiza estoque do livro
        book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() - 1);
        bookRepository.save(book);

        return loanRepository.save(emprestimo);
    }

    @SuppressWarnings("null")
    @Transactional
    public Emprestimo finalizarEmprestimo(String id) {
        Emprestimo emprestimo = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (emprestimo.getStatus() == EmprestimoStatus.RETORNADO) {
            throw new RuntimeException("Este empréstimo já foi finalizado anteriormente.");
        }

        // Atualiza status do empréstimo
        emprestimo.setStatus(EmprestimoStatus.RETORNADO);
        emprestimo.setDataDevolucao(LocalDateTime.now());
        emprestimo.setAtivo(false);
        emprestimo.setDataDevolucaoReal(LocalDate.now());

        // Devolve o exemplar ao estoque
        bookRepository.findById(emprestimo.getBookId()).ifPresent(book -> {
            book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() + 1);
            bookRepository.save(book);
        });

        return loanRepository.save(emprestimo);
    }

    @SuppressWarnings("null")
    @Transactional
    public Emprestimo renovar(String id) {

        Emprestimo emprestimo = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (!emprestimo.isAtivo() || emprestimo.getStatus() == EmprestimoStatus.RETORNADO) {
            throw new RuntimeException("Não é possível renovar um empréstimo finalizado.");
        }

        emprestimo.setDataVencimento(emprestimo.getDataVencimento().plusDays(7));

        return loanRepository.save(emprestimo);
    }

    public List<Emprestimo> getAllLoans() {
        return loanRepository.findAll();
    }
}