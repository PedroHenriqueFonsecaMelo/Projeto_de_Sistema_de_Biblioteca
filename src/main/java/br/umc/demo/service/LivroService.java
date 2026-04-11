package br.umc.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import br.umc.demo.entity.Livro;
import br.umc.demo.repository.LivroRepository;

@Service
public class LivroService {
    @Autowired
    private LivroRepository bookRepository;

    public Livro cadastrarNovoMaterial(Livro book) {

        book.setExemplaresDisponiveis(book.getTotalExemplares());
        return bookRepository.save(book);
    }

    public Livro atualizarMaterial(String id, Livro bookAtualizado) {
        Livro existente = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        existente.setTitulo(bookAtualizado.getTitulo());
        existente.setAutor(bookAtualizado.getAutor());
        existente.setIsbn(bookAtualizado.getIsbn());

        int diferencaExemplares = bookAtualizado.getTotalExemplares() - existente.getTotalExemplares();
        existente.setTotalExemplares(bookAtualizado.getTotalExemplares());
        existente.setExemplaresDisponiveis(existente.getExemplaresDisponiveis() + diferencaExemplares);

        return bookRepository.save(existente);
    }

    @SuppressWarnings("null")
    public void atualizarEstoque(String bookId, int quantidadeAdicional) {
        Livro book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        book.setTotalExemplares(book.getTotalExemplares() + quantidadeAdicional);
        book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() + quantidadeAdicional);
        bookRepository.save(book);
    }

    public List<Livro> findAll() {
        return bookRepository.findAll();
    }

    public List<Livro> searchBooks(String query) {
        return bookRepository.findByTituloContainingIgnoreCase(query);
    }

    public void deleteByIsbn(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }
}
