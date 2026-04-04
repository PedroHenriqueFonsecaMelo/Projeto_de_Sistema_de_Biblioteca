package br.umc.demo.repository;

import br.umc.demo.entity.SupportTicket;
import br.umc.demo.entity.TicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SupportTicketRepository extends MongoRepository<SupportTicket, String> {

    List<SupportTicket> findByStatus(TicketStatus status);
}