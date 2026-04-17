package br.umc.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    @SuppressWarnings("null")
    public Livro atualizarMaterial(String id, Livro bookAtualizado) {
        Optional<Livro> optExistente = bookRepository.findById(id);
        if (!optExistente.isPresent()) {
            throw new RuntimeException("Livro não encontrado");
        }
        Livro existente = optExistente.get();

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
        Optional<Livro> optBook = bookRepository.findById(bookId);
        if (!optBook.isPresent()) {
            throw new RuntimeException("Livro não encontrado");
        }
        Livro book = optBook.get();

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
