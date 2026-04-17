package br.umc.demo.controller.api;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.umc.demo.entity.SupportTicket;
import br.umc.demo.entity.enums.TicketStatus;
import br.umc.demo.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
@PreAuthorize("hasRole('LIBRARIAN')")
public class TicketControler {
    
    @Autowired
    private TicketService ticketService;

    @PostMapping("/registrar")
    public ResponseEntity<SupportTicket> registrarDuvida(@RequestBody SupportTicket ticket, Principal principal) {
 

        ticket.setBibliotecarioId(principal.getName());
        ticket.setDataRegistro(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        SupportTicket novoTicket = ticketService.registrarDuvida(ticket);
        return ResponseEntity.ok(novoTicket);
    }

    @PatchMapping("/responder/{id}")
    public ResponseEntity<SupportTicket> responderTicket(
            @PathVariable String id,
            @RequestParam String resposta,
            Principal principal) {

        SupportTicket ticketRespondido = ticketService.responderTicket(id, resposta, principal.getName());
        return ResponseEntity.ok(ticketRespondido);
    }
}
