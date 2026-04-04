package br.umc.demo.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "reservations")
@Data
public class Reservation {
    @Id
    private String id;
    private String bookId;
    private String leitorId;
    private LocalDateTime dataSolicitacao;
    private Integer posicaoNaFila;
    private boolean notificado; // Se o bibliotecário já avisou que o livro chegou
    private boolean ativa = true;
}