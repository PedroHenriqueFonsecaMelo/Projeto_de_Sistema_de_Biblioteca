package br.umc.demo.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import br.umc.demo.dto.request.ReservationRequest;
import br.umc.demo.entity.Reserva;
import br.umc.demo.service.ReservaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("hasRole('LIBRARIAN')")
@RequiredArgsConstructor // Substitui o @Autowired e cria o construtor automaticamente
public class ReservaControler {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@RequestBody ReservationRequest req) {
        return ResponseEntity.ok(reservaService.criarNovaReserva(req.getLeitorId(), req.getBookId()));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Reserva>> obterFilaPorLivro(@PathVariable String bookId) {
        return ResponseEntity.ok(reservaService.buscarFilaPorLivro(bookId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Reserva>> obterReservasAtivas() {
        return ResponseEntity.ok(reservaService.listarAtivas());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable String id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }
}