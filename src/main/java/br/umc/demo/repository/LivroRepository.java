package br.umc.demo.repository;

import br.umc.demo.entity.Livro;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LivroRepository extends MongoRepository<Livro, String> {
    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    List<Livro> findByAutorContainingIgnoreCase(String autor);

    List<Livro> findByExemplaresDisponiveisLessThan(Integer limite);

    void deleteByIsbn(String isbn);
}