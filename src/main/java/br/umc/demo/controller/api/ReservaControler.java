package br.umc.demo.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import br.umc.demo.dto.request.ReservationRequest;
import br.umc.demo.entity.Reserva;
import br.umc.demo.service.ReservaService;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("hasRole('LIBRARIAN')")
public class ReservaControler {

    @Autowired
    private ReservaService reservationService;

    @PostMapping
    public ResponseEntity<Reserva> create(@RequestBody ReservationRequest req) {
        return ResponseEntity.ok(reservationService.solicitarReserva(req.getLeitorId(), req.getBookId()));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Reserva>> getQueue(@PathVariable String bookId) {
        return ResponseEntity.ok(reservationService.getFilaPorLivro(bookId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Reserva>> getActiveReservations() {

        return ResponseEntity.ok(reservationService.getTodasReservasAtivas());
    }

}