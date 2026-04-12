package br.umc.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.umc.demo.entity.Livro;
import br.umc.demo.entity.Reserva;
import br.umc.demo.repository.LivroRepository;
import br.umc.demo.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LivroRepository bookRepository;

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
    public Reserva criarNovaReserva(String usuarioNome, String bookId) {
        Livro book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com ID: " + bookId));

        if (book.isDisponivel()) {
            throw new RuntimeException("Este livro possui exemplares disponíveis. Use o Empréstimo.");
        }

        int proximaPosicao = (int) (reservaRepository.countByBookIdAndAtivaTrue(bookId) + 1);

        Reserva novaReserva = Reserva.builder()
                .usuarioNome(usuarioNome)
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
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada: " + id));
    }

    private void reorganizarFila(String bookId) {
        List<Reserva> fila = reservaRepository.findByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(bookId);

        for (int i = 0; i < fila.size(); i++) {
            Reserva r = fila.get(i);
            r.setPosicaoNaFila(i + 1);
            reservaRepository.save(r);
        }
    }
}