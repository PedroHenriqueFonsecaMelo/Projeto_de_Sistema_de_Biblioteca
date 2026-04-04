package br.umc.demo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.umc.demo.entity.Book;
import br.umc.demo.entity.Loan;
import br.umc.demo.entity.LoanStatus;
import br.umc.demo.repository.BookRepository;
import br.umc.demo.repository.LoanRepository;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public Loan realizarEmprestimo(String leitorId, String bookId, String bibliotecarioId) {

        long ativos = loanRepository.countByLeitorIdAndStatus(leitorId, LoanStatus.ACTIVE);
        if (ativos >= 3) {
            throw new RuntimeException("Leitor já atingiu o limite de 3 livros simultâneos.");
        }


        Book book = bookRepository.findById(bookId).orElseThrow();
        if (book.getExemplaresDisponiveis() <= 0) {
            throw new RuntimeException("Não há exemplares disponíveis para este material.");
        }


        Loan loan = new Loan();
        loan.setLeitorId(leitorId);
        loan.setBookId(bookId);
        loan.setBibliotecarioId(bibliotecarioId);
        loan.setDataEmprestimo(LocalDateTime.now());
        loan.setDataVencimento(LocalDateTime.now().plusDays(14)); // Regra de 14 dias
        loan.setStatus(LoanStatus.ACTIVE);


        book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() - 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan processarDevolucao(String loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        Book book = bookRepository.findById(loan.getBookId()).orElseThrow();

        loan.setDataDevolucao(LocalDateTime.now());
        

        if (loan.getDataDevolucao().isAfter(loan.getDataVencimento())) {
            long diasAtraso = ChronoUnit.DAYS.between(loan.getDataVencimento(), loan.getDataDevolucao());
            double valorMulta = diasAtraso * 2.00; // R$ 2,00 por dia de atraso
            loan.setMultaCalculada(valorMulta);
            loan.setStatus(LoanStatus.OVERDUE);
        } else {
            loan.setStatus(LoanStatus.RETURNED);
        }


        book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() + 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }
}
