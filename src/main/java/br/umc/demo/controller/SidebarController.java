package br.umc.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    
    @Autowired
    private TicketService ticketService;

    // --- DASHBOARD ---

    @SuppressWarnings("null")
    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {

        List<Emprestimo> todosEmprestimos = loanRepository.findAll();

        // 1. Métricas Básicas (Cards)
        long total = todosEmprestimos.size();
        long atrasado = loanRepository.countByStatus(EmprestimoStatus.ATRASADO);

        model.addAttribute("totalLoans", total);
        model.addAttribute("activeUsers", userRepository.count());
        model.addAttribute("overdueBooks", atrasado);

        // 2. Cálculo do Gráfico de Devoluções
        int percentualNoPrazo = 100;
        if (total > 0) {
            percentualNoPrazo = (int) (((total - atrasado) * 100) / total);
        }
        model.addAttribute("percentualNoPrazo", percentualNoPrazo);

        // 3. Lógica para Livros Mais Procurados
        Map<String, Integer> contagemMap = new HashMap<>();
        for (Emprestimo e : todosEmprestimos) {
            String idLivro = e.getBookId();
            if (idLivro != null) {
                int atual = contagemMap.getOrDefault(idLivro, 0);
                contagemMap.put(idLivro, atual + 1);
            }
        }

        List<Map<String, Object>> topBooks = new ArrayList<>();
        int maxEmprestimos = 0;

        for (Integer qtd : contagemMap.values()) {
            if (qtd > maxEmprestimos)
                maxEmprestimos = qtd;
        }

        for (Map.Entry<String, Integer> entry : contagemMap.entrySet()) {
            Map<String, Object> livroData = new HashMap<>();

            String tituloLivro = "Livro não encontrado";
            Optional<Livro> livroOpt = bookRepository.findById(entry.getKey());
            if (livroOpt.isPresent()) {
                tituloLivro = livroOpt.get().getTitulo();
            }

            livroData.put("titulo", tituloLivro);
            livroData.put("quantidadeEmprestimos", entry.getValue());

            int percentualProcura = 0;
            if (maxEmprestimos > 0) {
                percentualProcura = (entry.getValue() * 100) / maxEmprestimos;
            }
            livroData.put("percentualProcura", percentualProcura);

            topBooks.add(livroData);
        }

        topBooks.sort(new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                Integer q1 = (Integer) m1.get("quantidadeEmprestimos");
                Integer q2 = (Integer) m2.get("quantidadeEmprestimos");
                return q2.compareTo(q1);
            }
        });

        if (topBooks.size() > 5) {
            topBooks = topBooks.subList(0, 5);
        }

        model.addAttribute("topBooks", topBooks);

        return "Dashboard";
    }

    // --- ACERVO (BOOKS) ---

    @GetMapping("/livros/acervo")
    public String exibirPaginaAcervo(Model model) {
        List<Livro> todosLivros = bookService.findAll();
        List<LivroDTO> livrosDTO = new ArrayList<>();
        for (Livro livro : todosLivros) {
            livrosDTO.add(converterParaLivroDTO(livro));
        }

        model.addAttribute("livros", livrosDTO);
        return "Acervo";
    }

    @PostMapping("/livros/salvar")
    public String processarCadastroLivro(@ModelAttribute Livro livro) {
        // Assume-se que o service lida com lógica de novo vs edição
        bookService.cadastrarNovoMaterial(livro);
        return "redirect:/library/livros/acervo";
    }

    @DeleteMapping("/livros/remover")
    public String processarRemocaoLivro(@RequestParam("isbn") String isbn) {
        bookService.deleteByIsbn(isbn);
        return "redirect:/library/livros/acervo";
    }

    // --- EMPRÉSTIMOS (LOANS) ---

    @GetMapping("/emprestimos")
    public String exibirPaginaEmprestimos(Model model) {
        List<Emprestimo> emprestimosAtivos = loanRepository.findByStatus(EmprestimoStatus.ATIVO);
        List<Emprestimo> emprestimosAtrasados = processarMultasEAtrasos();

        List<Livro> livros = bookService.findAll();
        List<Usuario> usuarios = userRepository.findAll();

        Map<String, String> userNameMap = new HashMap<>();
        for (Usuario u : usuarios) {
            userNameMap.put(u.getId(), u.getNome());
        }

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

    @SuppressWarnings("null")
    @GetMapping("/reservas")
    public String exibirPaginaReservas(Model model) {
        try {
            List<Reserva> reservasAtivas = reservaService.listarAtivas();

            if (reservasAtivas == null)
                reservasAtivas = new ArrayList<>();

            for (Reserva reserva : reservasAtivas) {
                // Preenchimento Seguro do Usuário
                if (reserva.getUsuarioNome() == null || reserva.getUsuarioNome().isEmpty()) {
                    if (reserva.getLeitorId() != null) {
                        userRepository.findById(reserva.getLeitorId())
                                .ifPresentOrElse(
                                        u -> reserva.setUsuarioNome(u.getNome()),
                                        () -> reserva.setUsuarioNome("Usuário não encontrado"));
                    } else {
                        reserva.setUsuarioNome("ID de Leitor Ausente");
                    }
                }

                // Preenchimento Seguro do Livro
                if (reserva.getLivroTitulo() == null || reserva.getLivroTitulo().isEmpty()) {
                    if (reserva.getBookId() != null) {
                        bookRepository.findById(reserva.getBookId())
                                .ifPresentOrElse(
                                        l -> reserva.setLivroTitulo(l.getTitulo()),
                                        () -> reserva.setLivroTitulo("Livro não encontrado"));
                    } else {
                        reserva.setLivroTitulo("ID de Livro Ausente");
                    }
                }
            }

            model.addAttribute("reservas", reservasAtivas);
            model.addAttribute("livros", bookService.findAll());
            model.addAttribute("usuarios", userRepository.findAll());
            model.addAttribute("totalReservas", reservasAtivas.size());

            return "Reservas";
        } catch (Exception e) {

            System.err.println("Erro crítico ao carregar Reservas: " + e.getMessage());
            return "redirect:/library/dashboard";
        }
    }

    @PostMapping("/reservas/reservar")
    public String processarNovaReserva(@ModelAttribute Reserva reserva) {

        reserva.setDataSolicitacao(LocalDateTime.now());
        reservaService.salvar(reserva);
        return "redirect:/library/reservas";
    }

    @PostMapping("/reservas/liberar")
    public String processarLiberacaoReserva(@RequestParam("reservaId") String reservaId) {
        // Ajustado para 'concluirReserva' ou 'liberarReserva' conforme seu Service
        reservaService.concluirReserva(reservaId);
        return "redirect:/library/reservas";
    }

    @GetMapping("/reservas/deletar/{id}")
    public String deletarReserva(@PathVariable String id) {
        reservaService.cancelarReserva(id);
        return "redirect:/library/reservas";
    }

    // --- RELATÓRIOS ---

    @SuppressWarnings("null")
    @GetMapping("/relatorios")
    public String exibirPaginaRelatorios(Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        // 1. Pega os dados estatísticos do seu service
        Map<String, Object> stats = statService.getLibraryReport();
        model.addAllAttributes(stats);

        // 2. Busca todos os empréstimos para a tabela
        List<Emprestimo> emprestimos;

        if (inicio != null && fim != null) {
            // Ajusta para o início do dia (00:00:00) e fim do dia (23:59:59)
            LocalDateTime dataInicio = inicio.atStartOfDay();
            LocalDateTime dataFim = fim.atTime(LocalTime.MAX);
            emprestimos = loanRepository.findByDataEmprestimoBetween(dataInicio, dataFim);
        } else {
            emprestimos = loanRepository.findAll();
        }

        Map<String, Usuario> usuariosMap = new HashMap<>();
        for (Emprestimo e : emprestimos) {
            Optional<Usuario> optU = userRepository.findById(e.getLeitorId());
            if (optU.isPresent()) {
                usuariosMap.put(e.getLeitorId(), optU.get());
            }
        }

        Map<String, Livro> livrosMap = new HashMap<>();
        for (Emprestimo e : emprestimos) {
            Optional<Livro> optB = bookRepository.findById(e.getBookId());
            if (optB.isPresent()) {
                livrosMap.put(e.getBookId(), optB.get());
            }
        }

        model.addAttribute("movimentacoes", emprestimos);
        model.addAttribute("usuarios", usuariosMap);
        model.addAttribute("livros", livrosMap);

        return "Relatorio";
    }

    // --- CONTROLE DE USUARIOS ---
    @GetMapping("/controle")
    public String exibirControleAcesso(Model model) {
        List<Usuario> usuarios = userRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "Controle";
    }

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
        return "Tickets";
    }

    // --- MÉTODOS AUXILIARES (PRIVATE) ---

    private List<Emprestimo> processarMultasEAtrasos() {
        List<Emprestimo> atrasados = loanRepository.findByStatus(EmprestimoStatus.ATRASADO);
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