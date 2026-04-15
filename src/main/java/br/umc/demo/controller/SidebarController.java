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
import java.util.stream.Collectors;

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
import br.umc.demo.entity.enums.LoanStatus;
import br.umc.demo.entity.Reserva;
import br.umc.demo.entity.User;
import br.umc.demo.repository.EmprestimoRepository;
import br.umc.demo.repository.LivroRepository;
import br.umc.demo.repository.UserRepository;
import br.umc.demo.service.LivroService;
import br.umc.demo.service.ReservaService;
import br.umc.demo.service.StatService;
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

    // --- DASHBOARD ---

    @SuppressWarnings("null")
    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {

        List<Emprestimo> todosEmprestimos = loanRepository.findAll();

        // 1. Métricas Básicas (Cards)
        long total = todosEmprestimos.size();
        long atrasado = loanRepository.countByStatus(LoanStatus.ATRASADO);

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
            @Override
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
        List<Emprestimo> emprestimosAtivos = loanRepository.findByStatus(LoanStatus.ATIVO);
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

        List<Reserva> reservasAtivas = reservaService.listarAtivas();

        List<Livro> livros = bookService.findAll();
        List<User> usuarios = userRepository.findAll();

        model.addAttribute("reservas", reservasAtivas);
        model.addAttribute("livros", livros);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalReservas", reservasAtivas.size());

        return "Reservas";
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

        Map<String, User> usuariosMap = new HashMap<>();
        for (Emprestimo e : emprestimos) {
            userRepository.findById(e.getLeitorId()).ifPresent(u -> usuariosMap.put(e.getLeitorId(), u));
        }

        Map<String, Livro> livrosMap = new HashMap<>();
        for (Emprestimo e : emprestimos) {
            bookRepository.findById(e.getBookId()).ifPresent(b -> livrosMap.put(e.getBookId(), b));
        }

        model.addAttribute("movimentacoes", emprestimos);
        model.addAttribute("usuarios", usuariosMap);
        model.addAttribute("livros", livrosMap);

        return "Relatorio";
    }

    // --- CONTROLE DE USUARIOS ---
    @GetMapping("/controle")
    public String exibirControleAcesso(Model model) {
        List<User> usuarios = userRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "Controle";
    }

    // --- MÉTODOS AUXILIARES (PRIVATE) ---

    private List<Emprestimo> processarMultasEAtrasos() {
        List<Emprestimo> atrasados = loanRepository.findByStatus(LoanStatus.ATRASADO);
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