package br.umc.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LivroDTO {
    private String isbn;
    private String titulo;
    private String autor;
    private String status;
    private String localizacao;
    private String imagemUrl;
}
