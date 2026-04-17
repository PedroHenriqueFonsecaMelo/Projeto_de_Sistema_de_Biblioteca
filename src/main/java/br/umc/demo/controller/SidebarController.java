package br.umc.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import br.umc.demo.dto.LivroDTO;
import br.umc.demo.entity.Livro;
import br.umc.demo.entity.Emprestimo;
import br.umc.demo.entity.enums.EmprestimoStatus;
import br.umc.demo.entity.Reserva;
import br.umc.demo.entity.SupportTicket;
import br.umc.demo.entity.Usuario;
import br.umc.demo.repository.EmprestimoRepository;
import br.umc.demo.repository.LivroRepository;
import br.umc.demo.repository.UserRepository;
import br.umc.demo.service.LivroService;
import br.umc.demo.service.ReservaService;
import br.umc.demo.service.StatService;
import br.umc.demo.service.TicketService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/library")
public class SidebarController {

    private final LivroService bookService;
    private final EmprestimoRepository loanRepository;
    private final ReservaService reservaService;
    private final UserRepository userRepository;
    private final LivroRepository bookRepository;
    private final StatService statService;
    private final TicketService ticketService;

    // --- DASHBOARD (Visualização) ---
    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {
        List<Emprestimo> todosEmprestimos = loanRepository.findAll();
        long total = todosEmprestimos.size();
        long atrasado = loanRepository.countByStatus(EmprestimoStatus.ATRASADO);

        model.addAttribute("totalLoans", total);
        model.addAttribute("activeUsers", userRepository.count());
        model.addAttribute("overdueBooks", atrasado);

        int percentualNoPrazo = total > 0 ? (int) (((total - atrasado) * 100) / total) : 100;
        model.addAttribute("percentualNoPrazo", percentualNoPrazo);

        model.addAttribute("topBooks", calcularTopBooks(todosEmprestimos));
        return "Dashboard";
    }

    // --- ACERVO (Visualização) ---
    @GetMapping("/livros/acervo")
    public String exibirPaginaAcervo(Model model) {
        List<LivroDTO> livrosDTO = bookService.findAll().stream()
                .map(this::converterParaLivroDTO)
                .toList();

        model.addAttribute("livros", livrosDTO);
        return "Acervo";
    }

    // --- EMPRÉSTIMOS (Visualização) ---
    @GetMapping("/emprestimos")
    public String exibirPaginaEmprestimos(Model model) {
        List<Emprestimo> emprestimosAtivos = loanRepository.findByStatus(EmprestimoStatus.ATIVO);
        List<Emprestimo> emprestimosAtrasados = processarMultasEAtrasos();

        Map<String, String> userNameMap = new HashMap<>();
        userRepository.findAll().forEach(u -> userNameMap.put(u.getId(), u.getNome()));

        model.addAttribute("emprestimosAtivos", emprestimosAtivos);
        model.addAttribute("emprestimosAtrasados", emprestimosAtrasados);
        model.addAttribute("livros", bookService.findAll());
        model.addAttribute("usuarios", userRepository.findAll());
        model.addAttribute("userNameMap", userNameMap);

        return "Emprestimos";
    }

    // --- RESERVAS (Visualização) ---
    @GetMapping("/reservas")
    public String exibirPaginaReservas(Model model) {
        List<Reserva> reservasAtivas = reservaService.listarAtivasComNomes();
        model.addAttribute("reservas", reservasAtivas);
        model.addAttribute("livros", bookService.findAll());
        model.addAttribute("usuarios", userRepository.findAll());
        model.addAttribute("totalReservas", reservasAtivas.size());
        return "Reservas";
    }

    // --- RELATÓRIOS (Visualização) ---
    @SuppressWarnings("null")
    @GetMapping("/relatorios")
    public String exibirPaginaRelatorios(Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        model.addAllAttributes(statService.getLibraryReport());

        List<Emprestimo> emprestimos = (inicio != null && fim != null)
                ? loanRepository.findByDataEmprestimoBetween(inicio.atStartOfDay(), fim.atTime(LocalTime.MAX))
                : loanRepository.findAll();
        model.addAttribute("movimentacoes", emprestimos);

        Map<String, Usuario> usuariosMap = new HashMap<>();
        for (Usuario u : userRepository.findAll()) {
            usuariosMap.put(u.getId().toString(), u);
        }

        Map<String, Livro> livrosMap = new HashMap<>();
        for (Livro b : bookRepository.findAll()) {
            livrosMap.put(b.getId().toString(), b);
        }

        model.addAttribute("usuarios", usuariosMap);
        model.addAttribute("livros", livrosMap);

        return "Relatorio";
    }

    // --- TICKETS (Visualização) ---
    @SuppressWarnings("null")
    @GetMapping("/tickets")
    public String tickets(Model model) {
        List<SupportTicket> tickets = ticketService.listTickets();

        for (SupportTicket ticket : tickets) {
            if (ticket.getLeitorId() != null) {
                userRepository.findById(ticket.getLeitorId())
                        .ifPresentOrElse(
                                user -> ticket.setLeitorId(user.getNome()),
                                () -> ticket.setLeitorId("Usuário não encontrado"));
            } else {
                ticket.setLeitorId("ID ausente");
            }
        }
        model.addAttribute("tickets", tickets);
        model.addAttribute("totalTickets", tickets.size());
        model.addAttribute("usuarios", userRepository.findAll());
        return "Tickets";
    }

    // --- CONTROLE DE USUARIOS ---
    @GetMapping("/controle")
    public String exibirControleAcesso(Model model) {
        List<Usuario> usuarios = userRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "Controle";
    }

    // --- MÉTODOS AUXILIARES ---

    private LivroDTO converterParaLivroDTO(Livro b) {
        String statusLabel = b.isDisponivel() ? "Disponível" : "Indisponível";
        return LivroDTO.builder()
                .id(b.getId())
                .isbn(b.getIsbn())
                .titulo(b.getTitulo())
                .autor(b.getAutor())
                .status(statusLabel)
                .localizacao(b.getLocalizacaoFisica())
                .build();
    }

    private List<Emprestimo> processarMultasEAtrasos() {
        List<Emprestimo> atrasados = loanRepository.findByStatus(EmprestimoStatus.ATRASADO);
        for (Emprestimo loan : atrasados) {
            if (loan.getDataVencimento() != null && LocalDateTime.now().isAfter(loan.getDataVencimento())) {
                long dias = ChronoUnit.DAYS.between(loan.getDataVencimento(), LocalDateTime.now());
                loan.setMultaCalculada(dias * 1.0);
                loanRepository.save(loan);
            }
        }
        return atrasados;
    }

    @SuppressWarnings("null")
    private List<Map<String, Object>> calcularTopBooks(List<Emprestimo> todosEmprestimos) {
        Map<String, Integer> contagemMap = new HashMap<>();

        for (Emprestimo e : todosEmprestimos) {
            String idLivro = e.getBookId();
            if (idLivro != null) {
                contagemMap.put(idLivro, contagemMap.getOrDefault(idLivro, 0) + 1);
            }
        }

        int maxEmprestimos = contagemMap.values().stream()
                .max(Comparator.naturalOrder())
                .orElse(0);

        List<Map<String, Object>> topBooks = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : contagemMap.entrySet()) {
            Map<String, Object> livroData = new HashMap<>();

            String tituloLivro = bookRepository.findById(entry.getKey())
                    .map(Livro::getTitulo)
                    .orElse("Livro não encontrado");

            livroData.put("titulo", tituloLivro);
            livroData.put("quantidadeEmprestimos", entry.getValue());

            int percentualProcura = (maxEmprestimos > 0)
                    ? (entry.getValue() * 100) / maxEmprestimos
                    : 0;

            livroData.put("percentualProcura", percentualProcura);
            topBooks.add(livroData);
        }

        topBooks.sort((m1, m2) -> {
            Integer q1 = (Integer) m1.get("quantidadeEmprestimos");
            Integer q2 = (Integer) m2.get("quantidadeEmprestimos");
            return q2.compareTo(q1);
        });

        return topBooks.size() > 5 ? topBooks.subList(0, 5) : topBooks;
    }
}