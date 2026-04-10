package br.umc.demo.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.umc.demo.dto.LivroDTO;
import br.umc.demo.entity.Book;
import br.umc.demo.entity.Loan;
import br.umc.demo.entity.LoanStatus;
import br.umc.demo.entity.Reservation;
import br.umc.demo.entity.User;
import br.umc.demo.repository.LoanRepository;
import br.umc.demo.repository.UserRepository;
import br.umc.demo.service.BookService;
import br.umc.demo.service.ReservationService;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/livros")
@RequiredArgsConstructor // Cria o construtor com todos os campos 'final' (Lombok)
public class LivrosController {

    private final BookService bookService;
    private final LoanRepository loanRepository;
    private final ReservationService reservationService;
    private final UserRepository userRepository;

    @GetMapping("/emprestimos")
    public String paginaEmprestimos(Model model) {
        List<Loan> emprestimosAtivos = loanRepository.findByStatus(LoanStatus.ACTIVE);
        List<Loan> emprestimosAtrasados = new ArrayList<>();

        // Lógica de cálculo de multa para itens atrasados
        for (Loan loan : loanRepository.findByStatus(LoanStatus.OVERDUE)) {
            if (loan.getDataVencimento() != null
                    && LocalDateTime.now().isAfter(loan.getDataVencimento())
                    && loan.getMultaCalculada() == null) {

                long daysOverdue = ChronoUnit.DAYS.between(loan.getDataVencimento(), LocalDateTime.now());
                loan.setMultaCalculada(daysOverdue * 1.0); // R$ 1,00 por dia
                loanRepository.save(loan);
            }
            emprestimosAtrasados.add(loan);
        }

        List<Book> livros = bookService.findAll();
        List<User> usuarios = userRepository.findAll();

        Map<String, String> userNameMap = usuarios.stream()
            .collect(Collectors.toMap(User::getId, User::getNome));

        model.addAttribute("emprestimosAtivos", emprestimosAtivos);
        model.addAttribute("emprestimosAtrasados", emprestimosAtrasados);
        model.addAttribute("livros", livros);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("userNameMap", userNameMap);
        model.addAttribute("totalEmprestimos", emprestimosAtivos.size());
        model.addAttribute("totalAtrasados", emprestimosAtrasados.size());

        return "Emprestimos";
    }

    @GetMapping("/reservas")
    public String paginaReservas(Model model) {
        var reservasAtivas = reservationService.getTodasReservasAtivas();
        List<Book> livros = bookService.findAll();
        List<User> usuarios = userRepository.findAll();

        model.addAttribute("reservas", reservasAtivas);
        model.addAttribute("livros", livros);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalReservas", reservasAtivas.size());

        return "Reservas";
    }

    @PostMapping("/buscar")
    public String buscarLivro(@RequestParam("query") String query, Model model) {
        List<Book> livrosBuscados = bookService.searchBooks(query);
        model.addAttribute("livrosBuscados", livrosBuscados);
        model.addAttribute("queryBusca", query);
        return "Emprestimos";
    }

    @GetMapping("/acervo")
    public String paginaAcervo(Model model) {
        List<Book> allBooks = bookService.findAll();
        List<LivroDTO> livrosDTO = allBooks.stream()
                .map(this::toLivroDTO)
                .toList();

        model.addAttribute("livros", livrosDTO);
        return "Acervo";
    }

    @DeleteMapping("/remover")
    public String removerLivro(@RequestParam("isbn") String isbn) {
        bookService.deleteByIsbn(isbn);
        return "redirect:/livros/acervo";
    }

    @PostMapping("/salvar")
    public String salvarReserva(@ModelAttribute Reservation reserva) {
        reserva.setDataSolicitacao(LocalDateTime.now());

        reservationService.salvar(reserva);

        return "redirect:/livros/reservas";
    }

    private LivroDTO toLivroDTO(Book b) {
        String status = b.isDisponivel() ? "Disponível" : "Emprestado/Reservado";
        return LivroDTO.builder()
                .isbn(b.getIsbn())
                .titulo(b.getTitulo())
                .autor(b.getAutor())
                .status(status)
                .localizacao(b.getLocalizacaoFisica())
                .imagemUrl(b.getImagemUrl())
                .build();
    }
}