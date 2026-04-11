package br.umc.demo.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "reservations")
@Data
public class Reserva {
    @Id
    private String id;
    private String bookId;
    private String leitorId;
    private LocalDateTime dataSolicitacao;
    private Integer posicaoNaFila;
    private boolean notificado;
    private boolean ativa = true;
}