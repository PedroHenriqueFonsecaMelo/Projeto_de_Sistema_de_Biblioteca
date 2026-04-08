package br.umc.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.umc.demo.entity.Book;
import br.umc.demo.entity.Loan;
import br.umc.demo.entity.LoanStatus;
import br.umc.demo.entity.User;
import br.umc.demo.repository.LoanRepository;
import br.umc.demo.repository.UserRepository;
import br.umc.demo.service.BookService;
import br.umc.demo.service.ReservationService;
import br.umc.demo.dto.LivroDTO;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/livros")
public class LivrosController {

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    // Rota para a página de Empréstimos
    @GetMapping("/emprestimos")
    public String paginaEmprestimos(Model model) {
        // Busca empréstimos ativos do banco de dados
        java.util.List<Loan> emprestimosAtivos = loanRepository.findByStatus(LoanStatus.ACTIVE);
        java.util.List<Loan> emprestimosAtrasados = loanRepository.findByStatus(LoanStatus.OVERDUE).stream()
            .peek(loan -> {
                if (loan.getDataVencimento() != null && java.time.LocalDateTime.now().isAfter(loan.getDataVencimento()) && loan.getMultaCalculada() == null) {
                    // Calcula multa: R$1 por dia de atraso (exemplo)
                    long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(loan.getDataVencimento(), java.time.LocalDateTime.now());
                    loan.setMultaCalculada(daysOverdue * 1.0);
                    loanRepository.save(loan);
                }
            })
            .collect(java.util.stream.Collectors.toList());
        
        // Busca todos os livros para contexto
        java.util.List<Book> livros = bookService.findAll();
        
        // Busca todos os usuários para contexto
        java.util.List<User> usuarios = userRepository.findAll();
        
        // Adiciona ao modelo
        model.addAttribute("emprestimosAtivos", emprestimosAtivos);
        model.addAttribute("emprestimosAtrasados", emprestimosAtrasados);
        model.addAttribute("livros", livros);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalEmprestimos", emprestimosAtivos.size());
        model.addAttribute("totalAtrasados", emprestimosAtrasados.size());
        
        return "Emprestimos";
    }

    // Rota para a página de Reservas
    @GetMapping("/reservas")
    public String paginaReservas(Model model) {
        // Busca todas as reservas ativas
        var reservasAtivas = reservationService.getTodasReservasAtivas();
        
        // Busca todos os livros para contexto
        java.util.List<Book> livros = bookService.findAll();
        
        // Busca todos os usuários para contexto
        java.util.List<User> usuarios = userRepository.findAll();
        
        // Adiciona ao modelo
        model.addAttribute("reservas", reservasAtivas);
        model.addAttribute("livros", livros);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalReservas", reservasAtivas.size());
        
        return "Reservas";
    }

    // Exemplo de busca (usado no Header de Empréstimos)
    @PostMapping("/buscar")
    public String buscarLivro(@RequestParam("query") String query, Model model) {
        // Busca livros que correspondem ao query
        java.util.List<Book> livrosBuscados = bookService.searchBooks(query);
        
        model.addAttribute("livrosBuscados", livrosBuscados);
        model.addAttribute("queryBusca", query);
        
        return "Emprestimos";
    }

@GetMapping("/acervo")
    public String paginaAcervo(Model model) {
        List<Book> allBooks = bookService.findAll();
        List<LivroDTO> livros = allBooks.stream().map(this::toLivroDTO).collect(Collectors.toList());
        model.addAttribute("livros", livros);
        return "Acervo";
    }

    private LivroDTO toLivroDTO(Book b) {
        String status = b.isDisponivel() ? "Disponível" : "Emprestado/Reservado";
        return new LivroDTO(b.getIsbn(), b.getTitulo(), b.getAutor(), status, b.getLocalizacaoFisica(), b.getImagemUrl());
    }
}
