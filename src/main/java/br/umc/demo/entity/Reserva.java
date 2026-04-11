package br.umc.demo.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    private String id;

    private String bookId;
    private String leitorId;

    private String usuarioNome;
    private String livroTitulo;

    private LocalDateTime dataSolicitacao;
    private Integer posicaoNaFila;
    
    @Builder.Default
    private boolean ativa = true;
    
    @Builder.Default
    private boolean notificado = false;

    public boolean podeRetirar() {
        return this.ativa && this.posicaoNaFila != null && this.posicaoNaFila == 1;
    }
}