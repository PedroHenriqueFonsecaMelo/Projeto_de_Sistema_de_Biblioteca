package br.umc.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "books")
@Data
public class Livro {
    @Id
    private String id;
    private String titulo;
    private String autor;
    private String editora;
    private Integer anoPublicacao;
    private String localizacaoFisica;
    private String isbn;
    private Integer totalExemplares;
    private Integer exemplaresDisponiveis;

    public boolean isDisponivel() {
        if (this.exemplaresDisponiveis == null)
            return false;
        return this.exemplaresDisponiveis != null && this.exemplaresDisponiveis > 0;
    }
}
