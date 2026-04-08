package br.umc.demo.dto;

public class LivroDTO {
    private String isbn;
    private String titulo;
    private String autor;
    private String status;
    private String localizacao;
    private String imagemUrl;

    public LivroDTO(String isbn, String titulo, String autor, String status, String localizacao, String imagemUrl) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.status = status;
        this.localizacao = localizacao;
        this.imagemUrl = imagemUrl;
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getStatus() {
        return status;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }
}
