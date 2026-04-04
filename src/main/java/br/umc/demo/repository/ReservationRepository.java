package br.umc.demo.repository;

import br.umc.demo.entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    

    long countByBookIdAndAtivaTrue(String bookId);


    List<Reservation> findByBookIdAndAtivaTrueOrderByPosicaoNaFilaAsc(String bookId);


    List<Reservation> findByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(String bookId);


    Optional<Reservation> findFirstByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(String bookId);


    List<Reservation> findByAtivaTrueOrderByDataSolicitacaoAsc();
}