package br.umc.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import br.umc.demo.entity.Book;
import br.umc.demo.repository.BookRepository;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public Book cadastrarNovoMaterial(Book book) {

        book.setExemplaresDisponiveis(book.getTotalExemplares());
        return bookRepository.save(book);
    }

    @SuppressWarnings("null")
    public void atualizarEstoque(String bookId, int quantidadeAdicional) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        
        book.setTotalExemplares(book.getTotalExemplares() + quantidadeAdicional);
        book.setExemplaresDisponiveis(book.getExemplaresDisponiveis() + quantidadeAdicional);
        bookRepository.save(book);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.findByTituloContainingIgnoreCase(query);
    }
}
