package br.umc.demo.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "support_tickets")
@Data
public class SupportTicket {
    @Id
    private String id;
    private String leitorId;
    private String assunto;
    private String mensagem;
    private LocalDateTime dataRegistro;
    private TicketStatus status;
    private String respostaBibliotecario;
    private String bibliotecarioId;
}