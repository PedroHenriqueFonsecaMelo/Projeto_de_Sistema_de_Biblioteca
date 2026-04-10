package br.umc.demo.repository;

import br.umc.demo.entity.Loan;
import br.umc.demo.entity.LoanStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends MongoRepository<Loan, String> {

    long countByLeitorIdAndStatus(String leitorId, LoanStatus status);

    List<Loan> findByStatus(LoanStatus status);

    long countByStatus(LoanStatus status);

    List<Loan> findByLeitorId(String leitorId);

    long countByStatusAndDataVencimentoBefore(LoanStatus status, LocalDateTime date);
    
    List<Loan> findFirst10ByStatusOrderByDataEmprestimoDesc(LoanStatus status);
}
