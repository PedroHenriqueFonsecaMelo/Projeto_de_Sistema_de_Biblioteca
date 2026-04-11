package br.umc.demo.repository;

import br.umc.demo.entity.Reserva;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends MongoRepository<Reserva, String> {

    long countByBookIdAndAtivaTrue(String bookId);

    List<Reserva> findByBookIdAndAtivaTrueOrderByPosicaoNaFilaAsc(String bookId);

    List<Reserva> findByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(String bookId);

    Optional<Reserva> findFirstByBookIdAndAtivaTrueOrderByDataSolicitacaoAsc(String bookId);

    List<Reserva> findByAtivaTrueOrderByDataSolicitacaoAsc();
}