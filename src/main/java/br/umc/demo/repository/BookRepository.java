package br.umc.demo.repository;

import br.umc.demo.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findByTituloContainingIgnoreCase(String titulo);

    List<Book> findByAutorContainingIgnoreCase(String autor);

    List<Book> findByExemplaresDisponiveisLessThan(Integer limite);

    void deleteByIsbn(String isbn);
}