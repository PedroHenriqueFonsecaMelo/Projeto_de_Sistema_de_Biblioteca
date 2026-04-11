package br.umc.demo.repository;

import br.umc.demo.entity.Emprestimo;
import br.umc.demo.entity.enums.LoanStatus;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmprestimoRepository extends MongoRepository<Emprestimo, String> {

    long countByLeitorIdAndStatus(String leitorId, LoanStatus status);

    List<Emprestimo> findByStatus(LoanStatus status);

    long countByStatus(LoanStatus status);

    List<Emprestimo> findByLeitorId(String leitorId);

    long countByStatusAndDataVencimentoBefore(LoanStatus status, LocalDateTime date);

    List<Emprestimo> findFirst10ByStatusOrderByDataEmprestimoDesc(LoanStatus status);
}
