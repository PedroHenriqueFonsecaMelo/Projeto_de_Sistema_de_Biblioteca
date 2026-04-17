package br.umc.demo.repository;

import br.umc.demo.entity.Emprestimo;
import br.umc.demo.entity.enums.EmprestimoStatus;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmprestimoRepository extends MongoRepository<Emprestimo, String> {

    long countByLeitorIdAndStatus(String leitorId, EmprestimoStatus status);

    List<Emprestimo> findByStatus(EmprestimoStatus status);

    long countByStatus(EmprestimoStatus status);

    List<Emprestimo> findByLeitorId(String leitorId);

    long countByStatusAndDataVencimentoBefore(EmprestimoStatus status, LocalDateTime date);

    List<Emprestimo> findFirst10ByStatusOrderByDataEmprestimoDesc(EmprestimoStatus status);

    List<Emprestimo> findByDataEmprestimoBetween(LocalDateTime inicio, LocalDateTime fim);

}
