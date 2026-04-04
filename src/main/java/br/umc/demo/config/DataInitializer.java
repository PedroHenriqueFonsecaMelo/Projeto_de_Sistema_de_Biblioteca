package br.umc.demo.config;

import br.umc.demo.entity.*;
import br.umc.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                           BookRepository bookRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAll();
        bookRepository.deleteAll();

        User admin = new User();
        admin.setNome("Pedro Henrique (Bibliotecário)");
        admin.setEmail("admin@library.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setDocumentoIdentidade("12.345.678-9");
        admin.setRoles(Set.of("ROLE_LIBRARIAN")); 
        userRepository.save(admin);

        User leitor1 = new User();
        leitor1.setNome("João Silva");
        leitor1.setEmail("joao@email.com");
        leitor1.setPassword(passwordEncoder.encode("user123"));
        leitor1.setRoles(Set.of("ROLE_READER"));
        userRepository.save(leitor1);

        Book book1 = new Book();
        book1.setTitulo("Clean Code");
        book1.setAutor("Robert C. Martin");
        book1.setEditora("Prentice Hall");
        book1.setAnoPublicacao(2008);
        book1.setLocalizacaoFisica("Estante A1 - Computação");
        book1.setTotalExemplares(5);
        book1.setExemplaresDisponiveis(5);
        
        Book book2 = new Book();
        book2.setTitulo("Java: Como Programar");
        book2.setAutor("Deitel & Deitel");
        book2.setEditora("Pearson");
        book2.setAnoPublicacao(2016);
        book2.setLocalizacaoFisica("Estante B2 - Programação");
        book2.setTotalExemplares(3);
        book2.setExemplaresDisponiveis(3);

        Book book3 = new Book();
        book3.setTitulo("Entendendo Algoritmos");
        book3.setAutor("Aditya Bhargava");
        book3.setEditora("Novatec");
        book3.setAnoPublicacao(2017);
        book3.setLocalizacaoFisica("Estante A2 - Algoritmos");
        book3.setTotalExemplares(1);
        book3.setExemplaresDisponiveis(1);

        bookRepository.saveAll(Arrays.asList(book1, book2, book3));

        System.out.println("-----------------------------------------");
        System.out.println("SUCESSO: Dados de teste carregados no MongoDB!");
        System.out.println("Login Admin: admin@library.com / admin123");
        System.out.println("-----------------------------------------");
    }
}