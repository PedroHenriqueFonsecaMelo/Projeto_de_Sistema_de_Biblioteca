package br.umc.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import br.umc.demo.entity.Book;
import br.umc.demo.entity.Reservation;
import br.umc.demo.repository.BookRepository;
import br.umc.demo.repository.ReservationRepository;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public Reservation solicitarReserva(String leitorId, String bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();


        if (book.getExemplaresDisponiveis() > 0) {
            throw new RuntimeException("Ainda existem exemplares disponíveis. Realize um empréstimo direto.");
        }



        long posicao = reservationRepository.countByBookIdAndAtivaTrue(bookId) + 1;

        Reservation reservation = new Reservation();
        reservation.setLeitorId(leitorId);
        reservation.setBookId(bookId);
        reservation.setDataSolicitacao(LocalDateTime.now());
        reservation.setPosicaoNaFila((int) posicao);
        reservation.setAtiva(true);

        return reservationRepository.save(reservation);
    }


    @Transactional
    public void processarProximaReserva(String bookId) {

        Reservation proxima = reservationRepository.findFirstByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(bookId)
                .orElse(null);

        if (proxima != null) {

            proxima.setNotificado(true);
            proxima.setAtiva(false); // Reserva concluída/atendida
            reservationRepository.save(proxima);


            atualizarPosicoesFila(bookId);
        }
    }

    private void atualizarPosicoesFila(String bookId) {
        List<Reservation> filaRestante = reservationRepository
                .findByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(bookId);
        for (int i = 0; i < filaRestante.size(); i++) {
            Reservation r = filaRestante.get(i);
            r.setPosicaoNaFila(i + 1);
            reservationRepository.save(r);
        }
    }

    public List<Reservation> getFilaPorLivro(String bookId) {
        return reservationRepository.findByBookIdAndAtivaTrueOrderByPosicaoNaFilaAsc(bookId);
    }

    public List<Reservation> getTodasReservasAtivas() {
        return reservationRepository.findByAtivaTrueOrderByDataSolicitacaoAsc();
    }
}
