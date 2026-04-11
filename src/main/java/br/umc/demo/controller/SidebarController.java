package br.umc.demo.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.umc.demo.dto.LivroDTO;
import br.umc.demo.entity.Livro;
import br.umc.demo.entity.Emprestimo;
import br.umc.demo.entity.enums.LoanStatus;
import br.umc.demo.entity.Reserva;
import br.umc.demo.entity.User;
import br.umc.demo.repository.EmprestimoRepository;
import br.umc.demo.repository.UserRepository;
import br.umc.demo.service.LivroService;
import br.umc.demo.service.ReservaService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SidebarController {

    private final LivroService bookService;
    private final EmprestimoRepository loanRepository;
    private final ReservaService reservationService;
    private final UserRepository userRepository;

    // --- DASHBOARD ---

    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {
        model.addAttribute("totalLoans", loanRepository.count());
        model.addAttribute("activeUsers", userRepository.count());
        model.addAttribute("overdueBooks", loanRepository.countByStatus(LoanStatus.OVERDUE));
        return "Dashboard";
    }

    // --- ACERVO (BOOKS) ---

    @GetMapping("/livros/acervo")
    public String exibirPaginaAcervo(Model model) {
        List<Livro> todosLivros = bookService.findAll();
        List<LivroDTO> livrosDTO = todosLivros.stream()
                .map(this::converterParaLivroDTO)
                .toList();

        model.addAttribute("livros", livrosDTO);
        return "Acervo";
    }

    @PostMapping("/livros/salvar")
    public String processarCadastroLivro(@ModelAttribute Livro livro) {
        // Assume-se que o service lida com lógica de novo vs edição
        bookService.cadastrarNovoMaterial(livro); 
        return "redirect:/livros/acervo";
    }

    @DeleteMapping("/livros/remover")
    public String processarRemocaoLivro(@RequestParam("isbn") String isbn) {
        bookService.deleteByIsbn(isbn);
        return "redirect:/livros/acervo";
    }

    // --- EMPRÉSTIMOS (LOANS) ---

    @GetMapping("/emprestimos")
    public String exibirPaginaEmprestimos(Model model) {
        List<Emprestimo> emprestimosAtivos = loanRepository.findByStatus(LoanStatus.ACTIVE);
        List<Emprestimo> emprestimosAtrasados = processarMultasEAtrasos();

        List<Livro> livros = bookService.findAll();
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

    @PostMapping("/emprestimos/buscar")
    public String processarBuscaLivro(@RequestParam("query") String query, Model model) {
        List<Livro> livrosBuscados = bookService.searchBooks(query);
        model.addAttribute("livrosBuscados", livrosBuscados);
        model.addAttribute("queryBusca", query);
        return "Emprestimos";
    }

    // --- RESERVAS (RESERVATIONS) ---

    @GetMapping("/reservas")
    public String exibirPaginaReservas(Model model) {
        var reservasAtivas = reservationService.getTodasReservasAtivas();
        List<Livro> livros = bookService.findAll();
        List<User> usuarios = userRepository.findAll();

        model.addAttribute("reservas", reservasAtivas);
        model.addAttribute("livros", livros);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalReservas", reservasAtivas.size());

        return "Reservas";
    }

    @PostMapping("/reservar/reservas")
    public String processarNovaReserva(@ModelAttribute Reserva reserva) {
        reserva.setDataSolicitacao(LocalDateTime.now());
        reservationService.salvar(reserva);
        return "redirect:/reservas";
    }

    // --- RELATÓRIOS ---

    @GetMapping("/relatorios")
    public String exibirPaginaRelatorios() {
        return "relatorios";
    }

    // --- MÉTODOS AUXILIARES (PRIVATE) ---

    private List<Emprestimo> processarMultasEAtrasos() {
        List<Emprestimo> atrasados = loanRepository.findByStatus(LoanStatus.OVERDUE);
        for (Emprestimo loan : atrasados) {
            if (loan.getDataVencimento() != null
                    && LocalDateTime.now().isAfter(loan.getDataVencimento())
                    && loan.getMultaCalculada() == null) {

                long diasAtraso = ChronoUnit.DAYS.between(loan.getDataVencimento(), LocalDateTime.now());
                loan.setMultaCalculada(diasAtraso * 1.0); // R$ 1,00 por dia
                loanRepository.save(loan);
            }
        }
        return atrasados;
    }

    private LivroDTO converterParaLivroDTO(Livro b) {
        String status = b.isDisponivel() ? "Disponível" : "Emprestado/Reservado";
        return LivroDTO.builder()
                .isbn(b.getIsbn())
                .titulo(b.getTitulo())
                .autor(b.getAutor())
                .status(status)
                .localizacao(b.getLocalizacaoFisica())
                .build();
    }
}