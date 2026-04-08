package br.umc.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.umc.demo.entity.Loan;
import br.umc.demo.entity.Book;
import br.umc.demo.entity.LoanStatus;
import br.umc.demo.repository.BookRepository;
import br.umc.demo.repository.LoanRepository;
import br.umc.demo.repository.UserRepository;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    public Loan realizarEmprestimo(String leitorId, String bookId, String bibliotecarioId) {
        Loan loan = new Loan();
        loan.setLeitorId(leitorId);
        loan.setBibliotecarioId(bibliotecarioId);
        loan.setBookId(bookId);
        loan.setDataEmprestimo(LocalDateTime.now());
        loan.setDataVencimento(LocalDateTime.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);
        
        // Update book stock
        @SuppressWarnings("null")
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() - 1);
        bookRepository.save(book);
        
        return loanRepository.save(loan);
    }

    public Loan processarDevolucao(String id) {
        @SuppressWarnings("null")
        Loan loan = loanRepository.findById(id).orElseThrow();
        loan.setDataDevolucao(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
        
        // Update book
        @SuppressWarnings("null")
        Book book = bookRepository.findById(loan.getBookId()).orElseThrow();
        book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() + 1);
        bookRepository.save(book);
        
        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}

