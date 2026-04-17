package br.umc.demo.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.umc.demo.entity.enums.EmprestimoStatus;
import lombok.Data;

@Document(collection = "loans")
@Data
public class Emprestimo {
    @Id
    private String id;
    private String leitorId;
    private String bibliotecarioId;
    private String bookId;

    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataVencimento;
    private LocalDateTime dataDevolucao;

    private Double multaCalculada;
    private EmprestimoStatus status;

    private boolean ativo = true;
    private java.time.LocalDate dataDevolucaoReal;
}
