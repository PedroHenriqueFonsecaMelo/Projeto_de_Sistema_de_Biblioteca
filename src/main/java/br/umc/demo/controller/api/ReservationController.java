package br.umc.demo.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import br.umc.demo.dto.ReservationRequest;
import br.umc.demo.entity.Reservation;
import br.umc.demo.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("hasRole('LIBRARIAN')")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;


    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody ReservationRequest req) {
        return ResponseEntity.ok(reservationService.solicitarReserva(req.getLeitorId(), req.getBookId()));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Reservation>> getQueue(@PathVariable String bookId) {
        return ResponseEntity.ok(reservationService.getFilaPorLivro(bookId));
    }


    @GetMapping("/active")
    public ResponseEntity<List<Reservation>> getActiveReservations() {

        return ResponseEntity.ok(reservationService.getTodasReservasAtivas());
    }
    
}