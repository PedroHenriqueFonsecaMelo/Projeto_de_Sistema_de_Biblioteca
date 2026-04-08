package br.umc.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import br.umc.demo.entity.SupportTicket;
import br.umc.demo.entity.TicketStatus;
import br.umc.demo.repository.SupportTicketRepository;

@Service
public class TicketService {
    @Autowired
    private SupportTicketRepository ticketRepository;

    public SupportTicket registrarDuvida(SupportTicket ticket) {
        ticket.setDataRegistro(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }


    @SuppressWarnings("null")
    public SupportTicket responderTicket(String ticketId, String resposta, String bibliotecarioId) {
        SupportTicket ticket = ticketRepository.findById(ticketId).orElseThrow();
        
        ticket.setRespostaBibliotecario(resposta);
        ticket.setBibliotecarioId(bibliotecarioId);
        ticket.setStatus(TicketStatus.CLOSED);
        
        return ticketRepository.save(ticket);
    }
}
