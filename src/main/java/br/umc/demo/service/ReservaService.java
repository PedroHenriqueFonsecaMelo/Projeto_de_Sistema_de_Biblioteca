package br.umc.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import br.umc.demo.entity.Livro;
import br.umc.demo.entity.Reserva;
import br.umc.demo.repository.LivroRepository;
import br.umc.demo.repository.ReservaRepository;

@Service
public class ReservaService {
    @Autowired
    private ReservaRepository reservationRepository;
    @Autowired
    private LivroRepository bookRepository;

    @SuppressWarnings("null")
    @Transactional
    public Reserva solicitarReserva(String leitorId, String bookId) {
        Livro book = bookRepository.findById(bookId).orElseThrow();

        if (book.getExemplaresDisponiveis() > 0) {
            throw new RuntimeException("Ainda existem exemplares disponíveis. Realize um empréstimo direto.");
        }

        long posicao = reservationRepository.countByBookIdAndAtivaTrue(bookId) + 1;

        Reserva reservation = new Reserva();
        reservation.setLeitorId(leitorId);
        reservation.setBookId(bookId);
        reservation.setDataSolicitacao(LocalDateTime.now());
        reservation.setPosicaoNaFila((int) posicao);
        reservation.setAtiva(true);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void processarProximaReserva(String bookId) {

        Reserva proxima = reservationRepository.findFirstByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(bookId)
                .orElse(null);

        if (proxima != null) {

            proxima.setNotificado(true);
            proxima.setAtiva(false);
            reservationRepository.save(proxima);

            atualizarPosicoesFila(bookId);
        }
    }

    private void atualizarPosicoesFila(String bookId) {
        List<Reserva> filaRestante = reservationRepository
                .findByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(bookId);
        for (int i = 0; i < filaRestante.size(); i++) {
            Reserva r = filaRestante.get(i);
            r.setPosicaoNaFila(i + 1);
            reservationRepository.save(r);
        }
    }

    public List<Reserva> getFilaPorLivro(String bookId) {
        return reservationRepository.findByBookIdAndAtivaTrueOrderByPosicaoNaFilaAsc(bookId);
    }

    public List<Reserva> getTodasReservasAtivas() {
        return reservationRepository.findByAtivaTrueOrderByDataSolicitacaoAsc();
    }

    @SuppressWarnings("null")
    @Transactional
    public void salvar(Reserva reserva) {

        reservationRepository.save(reserva);
    }
}
