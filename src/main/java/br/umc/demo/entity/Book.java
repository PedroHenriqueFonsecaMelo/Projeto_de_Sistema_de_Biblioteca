package br.umc.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "books")
@Data
public class Book {
    @Id
    private String id;
    private String titulo;
    private String autor;
    private String editora;
    private Integer anoPublicacao;
    private String localizacaoFisica; // Ex: Corredor A, Estante 3
    
    private Integer totalExemplares;
    private Integer exemplaresDisponiveis; // Calculado: Total - Emprestados
    
    public boolean isDisponivel() {
        return this.exemplaresDisponiveis > 0;
    }
}