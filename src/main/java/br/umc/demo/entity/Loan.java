package br.umc.demo.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "loans")
@Data
public class Loan {
    @Id
    private String id;
    private String leitorId; // ID do User (Reader)
    private String bibliotecarioId; // ID do User que realizou a operação
    private String bookId;
    
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataVencimento; // DataEmprestimo + 14 dias
    private LocalDateTime dataDevolucao; // Preenchido no momento da entrega
    
    private Double multaCalculada;
    private LoanStatus status; // ACTIVE, RETURNED, OVERDUE
}
