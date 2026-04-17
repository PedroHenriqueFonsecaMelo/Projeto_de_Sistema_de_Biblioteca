package br.umc.demo.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import br.umc.demo.dto.request.ReservaRequest;
import br.umc.demo.entity.Reserva;
import br.umc.demo.service.ReservaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@PreAuthorize("hasRole('LIBRARIAN')")
@RequiredArgsConstructor
public class ReservaControler {

    private final ReservaService reservaService;

    @GetMapping("/active")
    public ResponseEntity<List<Reserva>> obterReservasAtivas() {
        return ResponseEntity.ok(reservaService.listarAtivas());
    }

    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@RequestBody ReservaRequest req) {
        Reserva novaReserva = reservaService.criarNovaReserva(req.getLeitorId(), req.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaReserva);
    }

    @PatchMapping("/liberar/{id}")
    public ResponseEntity<Void> liberarReserva(@PathVariable String id) {
        reservaService.concluirReserva(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable String id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Reserva>> obterFilaPorLivro(@PathVariable String bookId) {
        return ResponseEntity.ok(reservaService.buscarFilaPorLivro(bookId));
    }
}