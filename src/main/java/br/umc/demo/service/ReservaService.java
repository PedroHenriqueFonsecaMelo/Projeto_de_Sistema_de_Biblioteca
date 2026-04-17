package br.umc.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.umc.demo.entity.Livro;
import br.umc.demo.entity.Reserva;
import br.umc.demo.entity.Usuario;
import br.umc.demo.repository.LivroRepository;
import br.umc.demo.repository.ReservaRepository;
import br.umc.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LivroRepository bookRepository;
    private final UserRepository userRepository;

    // --- CONSULTAS (GETTERS) ---

    public List<Reserva> listarAtivas() {
        return reservaRepository.findByAtivaTrueOrderByDataSolicitacaoAsc();
    }

    public List<Reserva> buscarFilaPorLivro(String bookId) {
        return reservaRepository.findByBookIdAndAtivaTrueOrderByPosicaoNaFilaAsc(bookId);
    }

    // --- OPERAÇÕES PRINCIPAIS ---

    @SuppressWarnings("null")
    @Transactional
    public Reserva criarNovaReserva(String leitorId, String bookId) { 

        Optional<Livro> optBook = bookRepository.findById(bookId);
        if (!optBook.isPresent()) {
            throw new RuntimeException("Livro não encontrado com ID: " + bookId);
        }
        Livro book = optBook.get();

        Optional<Usuario> optUser = userRepository.findById(leitorId);
        if (!optUser.isPresent()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + leitorId);
        }
        Usuario usuario = optUser.get();

        if (book.isDisponivel()) {
            throw new RuntimeException("Este livro possui exemplares disponíveis. Use o Empréstimo.");
        }

        int proximaPosicao = (int) (reservaRepository.countByBookIdAndAtivaTrue(bookId) + 1);

        Reserva novaReserva = Reserva.builder()
                .leitorId(leitorId)
                .usuarioNome(usuario.getNome())
                .bookId(bookId)
                .livroTitulo(book.getTitulo())
                .dataSolicitacao(LocalDateTime.now())
                .posicaoNaFila(proximaPosicao)
                .ativa(true)
                .build();

        return reservaRepository.save(novaReserva);
    }

    @Transactional
    public void concluirReserva(String reservaId) {
        Reserva reserva = buscarPorId(reservaId);

        reserva.setNotificado(true);
        reserva.setAtiva(false);
        reservaRepository.save(reserva);

        reorganizarFila(reserva.getBookId());
    }

    @Transactional
    public void cancelarReserva(String reservaId) {
        Reserva reserva = buscarPorId(reservaId);
        String bookId = reserva.getBookId();

        reservaRepository.delete(reserva);
        reorganizarFila(bookId);
    }

    @SuppressWarnings("null")
    @Transactional
    public void salvar(Reserva reserva) {
        reservaRepository.save(reserva);
    }

    // --- AUXILIARES ---

    @SuppressWarnings("null")
    private Reserva buscarPorId(String id) {
        Optional<Reserva> optReserva = reservaRepository.findById(id);
        if (!optReserva.isPresent()) {
            throw new RuntimeException("Reserva não encontrada: " + id);
        }
        return optReserva.get();
    }

    private void reorganizarFila(String bookId) {
        List<Reserva> fila = reservaRepository.findByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(bookId);

        for (int i = 0; i < fila.size(); i++) {
            Reserva r = fila.get(i);
            r.setPosicaoNaFila(i + 1);
            reservaRepository.save(r);
        }
    }

    @SuppressWarnings("null")
    public List<Reserva> listarAtivasComNomes() {

        List<Reserva> reservasAtivas = listarAtivas();

        if (reservasAtivas == null) {
            reservasAtivas = new ArrayList<>();
        }

        for (Reserva reserva : reservasAtivas) {
            if (reserva.getUsuarioNome() == null || reserva.getUsuarioNome().isEmpty()) {
                if (reserva.getLeitorId() != null) {
                    Optional<Usuario> optUsuario = userRepository.findById(reserva.getLeitorId());

                    if (optUsuario.isPresent()) {
                        Usuario u = optUsuario.get();
                        reserva.setUsuarioNome(u.getNome());
                    } else {
                        reserva.setUsuarioNome("Usuário não encontrado");
                    }
                } else {
                    reserva.setUsuarioNome("ID de Leitor Ausente");
                }
            }

            if (reserva.getLivroTitulo() == null || reserva.getLivroTitulo().isEmpty()) {
                if (reserva.getBookId() != null) {
                    Optional<Livro> optLivro = bookRepository.findById(reserva.getBookId());

                    if (optLivro.isPresent()) {
                        Livro l = optLivro.get();
                        reserva.setLivroTitulo(l.getTitulo());
                    } else {
                        reserva.setLivroTitulo("Livro não encontrado");
                    }
                } else {
                    reserva.setLivroTitulo("ID de Livro Ausente");
                }
            }
        }

        return reservasAtivas;
    }
}