package br.umc.demo.config;

import br.umc.demo.entity.*;
import br.umc.demo.entity.enums.LoanStatus;
import br.umc.demo.entity.enums.TicketStatus;
import br.umc.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final LivroRepository bookRepository;
        private final EmprestimoRepository loanRepository;
        private final ReservaRepository reservationRepository;
        private final SupportTicketRepository supportTicketRepository;
        private final PasswordEncoder passwordEncoder;

        public DataInitializer(UserRepository userRepository,
                        LivroRepository bookRepository,
                        EmprestimoRepository loanRepository,
                        ReservaRepository reservationRepository,
                        SupportTicketRepository supportTicketRepository,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.bookRepository = bookRepository;
                this.loanRepository = loanRepository;
                this.reservationRepository = reservationRepository;
                this.supportTicketRepository = supportTicketRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @SuppressWarnings("null")
        @Override
        public void run(String... args) throws Exception {

                userRepository.deleteAll();
                bookRepository.deleteAll();
                loanRepository.deleteAll();
                reservationRepository.deleteAll();
                supportTicketRepository.deleteAll();

                User admin = new User();
                admin.setNome("Pedro Henrique (Bibliotecário)");
                admin.setEmail("admin@library.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setDocumentoIdentidade("123.456.789-00");
                admin.setRoles(Set.of("LIBRARIAN"));
                String adminId = userRepository.save(admin).getId();

                String[] leitorNames = { "João Silva", "Maria Oliveira", "Carlos Santos", "Ana Costa", "Pedro Almeida",
                                "Julia Ferreira", "Lucas Pereira", "Fernanda Lima", "Rafael Souza", "Camila Rocha",
                                "Diego Martins" };
                String[] leitorEmails = { "joao@email.com", "maria@email.com", "carlos@email.com", "ana@email.com",
                                "pedro2@email.com", "julia@email.com", "lucas@email.com", "fernanda@email.com",
                                "rafael@email.com",
                                "camila@email.com", "diego@email.com" };
                List<User> users = new ArrayList<>();
                for (int i = 0; i < leitorNames.length; i++) {
                        User u = new User();
                        u.setNome(leitorNames[i]);
                        u.setEmail(leitorEmails[i]);
                        u.setPassword(passwordEncoder.encode("user123"));
                        u.setDocumentoIdentidade((i + 1) + "00.111.222-" + String.format("%02d", i + 1));
                        u.setRoles(Set.of("READER"));
                        users.add(u);
                }

                List<User> savedUsers = userRepository.saveAll(users);
                System.out.println("Saved " + savedUsers.size() + " users");

                String[] userIds = savedUsers.stream().map(User::getId).toArray(String[]::new);

                Livro[] books = {
                                createBook("Clean Code", "Robert C. Martin", "Prentice Hall", 2008, "Est A1",
                                                "9780132350884", 5, 3),
                                createBook("Java: Como Programar", "Deitel", "Pearson", 2016, "Est B2", "9780134458185",
                                                3, 2),
                                createBook("Entendendo Algoritmos", "Aditya Bhargava", "Novatec", 2017, "Est A2",
                                                "9788575226772", 1,
                                                0),
                                createBook("O Silêncio dos Livros", "Mariana Fontes", "Editora X", 2023, "Prat A-12",
                                                "9783161484100",
                                                4, 4),
                                createBook("Arquitetura de Dados", "Ricardo Mendes", "Editora Y", 2022, "Devol 14/10",
                                                "9788535902773",
                                                2, 0),
                                createBook("Design para Curadores", "Elena Valieri", "Editora Z", 2021, "Prat C-04",
                                                "9780132350884", 3,
                                                3),
                                createBook("Botânica Sistêmica", "Dr. Arthur Silva", "Editora W", 2020, "Fila:2",
                                                "9781491950296", 2,
                                                0),
                                createBook("Navegando na Incerteza", "Juliana Prazeres", "Editora V", 2023, "Prat B-09",
                                                "9783161484100", 5, 5),
                                createBook("O Senhor dos Anéis", "J.R.R. Tolkien", "HarperCollins", 1954, "Est F1",
                                                "9780544003415", 10,
                                                2),
                                createBook("1984", "George Orwell", "Secker & Warburg", 1949, "Est F2", "9780451524935",
                                                8, 1),
                                createBook("Dom Casmurro", "Machado de Assis", "Garnier", 1899, "Est B1",
                                                "9788520903355", 6, 4),
                                createBook("Harry Potter 1", "J.K. Rowling", "Bloomsbury", 1997, "Est J1",
                                                "9780747532699", 4, 3),
                                createBook("Spring Boot in Action", "Craig Walls", "Manning", 2015, "Est S1",
                                                "9781617292541", 2, 2),
                                createBook("MongoDB Applied", "Rick Copeland", "O'Reilly", 2011, "Est M1",
                                                "9781449311512", 1, 1),
                                createBook("Refatoração", "Martin Fowler", "Addison-Wesley", 1999, "Est R1",
                                                "9788574112439", 3, 1),

                                createBook("Clean Architecture", "Robert C. Martin", "Prentice Hall", 2017, "Est A3",
                                                "9780134494166",
                                                4, 4),
                                createBook("The Pragmatic Programmer", "Hunt & Thomas", "Addison-Wesley", 1999,
                                                "Est P1",
                                                "9780201616224", 5, 2),
                                createBook("Head First Design Patterns", "Freeman", "O'Reilly", 2004, "Est D1",
                                                "9780596007126", 3, 3),
                                createBook("Grokking Algorithms", "Aditya Bhargava", "Manning", 2016, "Est G1",
                                                "9781617292237", 2, 1),
                                createBook("Python Crash Course", "Eric Matthes", "No Starch Press", 2015, "Est Py1",
                                                "9781593276034",
                                                6, 5),
                                createBook("Introduction to Algorithms", "CLRS", "MIT Press", 2009, "Est I1",
                                                "9780262033848", 1, 0),
                                createBook("Design Patterns", "Gang of Four", "Addison-Wesley", 1994, "Est DP1",
                                                "9780201633610", 4, 2),
                                createBook("Domain-Driven Design", "Eric Evans", "Addison-Wesley", 2003, "Est D2",
                                                "9780321125217", 2,
                                                2),
                                createBook("Site Reliability Engineering", "Google", "O'Reilly", 2016, "Est SRE1",
                                                "9781491929114", 3,
                                                3),
                                createBook("The Phoenix Project", "Kim et al", "IT Revolution", 2013, "Est Ph1",
                                                "9780988262591", 5, 1)
                };

                List<Livro> savedBooks = bookRepository.saveAll(Arrays.asList(books));
                System.out.println("Saved " + savedBooks.size() + " books");

                String[] bookIds = savedBooks.stream().map(Livro::getId).toArray(String[]::new);

                Emprestimo[] loans = {
                                createLoan(userIds[1], bookIds[0], LocalDateTime.now().minusDays(5),
                                                LocalDateTime.now().plusDays(9),
                                                LoanStatus.ACTIVE),
                                createLoan(userIds[2], bookIds[1], LocalDateTime.now().minusDays(20),
                                                LocalDateTime.now().minusDays(1),
                                                LoanStatus.OVERDUE, 5.0),
                                createLoan(userIds[3], bookIds[8], LocalDateTime.now().minusDays(10),
                                                LocalDateTime.now().minusDays(3),
                                                LoanStatus.RETURNED),

                                createLoan(userIds[4], bookIds[3], LocalDateTime.now().minusDays(3),
                                                LocalDateTime.now().plusDays(11),
                                                LoanStatus.ACTIVE),
                                createLoan(userIds[5], bookIds[9], LocalDateTime.now().minusDays(25),
                                                LocalDateTime.now().minusDays(2),
                                                LoanStatus.OVERDUE, 8.0),
                                createLoan(userIds[6], bookIds[2], LocalDateTime.now().minusDays(7),
                                                LocalDateTime.now().plusDays(7),
                                                LoanStatus.ACTIVE),
                                createLoan(userIds[7], bookIds[10], LocalDateTime.now().minusDays(30),
                                                LocalDateTime.now().minusDays(10), LoanStatus.OVERDUE, 10.0),
                                createLoan(userIds[8], bookIds[4], LocalDateTime.now().minusDays(12),
                                                LocalDateTime.now(),
                                                LoanStatus.RETURNED),
                                createLoan(userIds[9], bookIds[5], LocalDateTime.now().minusDays(2),
                                                LocalDateTime.now().plusDays(12),
                                                LoanStatus.ACTIVE),
                                createLoan(userIds[10], bookIds[6], LocalDateTime.now().minusDays(18),
                                                LocalDateTime.now().minusDays(4),
                                                LoanStatus.OVERDUE, 6.0),
                                createLoan(adminId, bookIds[7], LocalDateTime.now().minusDays(8),
                                                LocalDateTime.now().plusDays(6),
                                                LoanStatus.ACTIVE),
                                createLoan(userIds[1], bookIds[11], LocalDateTime.now().minusDays(15),
                                                LocalDateTime.now().minusDays(5),
                                                LoanStatus.RETURNED),
                                createLoan(userIds[2], bookIds[12], LocalDateTime.now().minusDays(4),
                                                LocalDateTime.now().plusDays(10),
                                                LoanStatus.ACTIVE),
                                createLoan(userIds[3], bookIds[13], LocalDateTime.now().minusDays(22),
                                                LocalDateTime.now().minusDays(1),
                                                LoanStatus.OVERDUE, 7.0),
                                createLoan(userIds[4], bookIds[14], LocalDateTime.now().minusDays(6),
                                                LocalDateTime.now().plusDays(8),
                                                LoanStatus.ACTIVE),
                                createLoan(adminId, bookIds[15], LocalDateTime.now().minusDays(1),
                                                LocalDateTime.now().plusDays(13),
                                                LoanStatus.ACTIVE)
                };
                loanRepository.saveAll(Arrays.asList(loans));

                Reserva[] reservations = {
                                createReservation(bookIds[2], userIds[1], 1),
                                createReservation(bookIds[2], userIds[2], 2),
                                createReservation(bookIds[6], userIds[3], 1),

                                createReservation(bookIds[9], userIds[4], 1),
                                createReservation(bookIds[0], userIds[5], 1),
                                createReservation(bookIds[1], userIds[6], 1),
                                createReservation(bookIds[3], userIds[7], 1),
                                createReservation(bookIds[8], userIds[8], 2),
                                createReservation(bookIds[10], userIds[9], 1),
                                createReservation(bookIds[4], userIds[10], 1),
                                createReservation(bookIds[5], adminId, 1),
                                createReservation(bookIds[12], adminId, 1)
                };
                reservationRepository.saveAll(Arrays.asList(reservations));

                SupportTicket[] tickets = {
                                createTicket(userIds[1], "Problema com empréstimo", "Livro não aparece como devolvido",
                                                TicketStatus.OPEN),
                                createTicket(userIds[2], "Dúvida reserva", "Como funciona a fila?",
                                                TicketStatus.IN_PROGRESS),
                                createTicket(userIds[3], "Sugestão compra", "Mais livros de Java", TicketStatus.CLOSED,
                                                "Adicionado ao orçamento"),

                                createTicket(userIds[4], "Multa errada", "Calculo incorreto", TicketStatus.OPEN),
                                createTicket(userIds[5], "Login problema", "Esqueci senha", TicketStatus.IN_PROGRESS),
                                createTicket(userIds[6], "Livro danificado", "Clean Code rasgado", TicketStatus.CLOSED,
                                                "Novo exemplar enviado"),
                                createTicket(userIds[7], "Horário", "A Biblioteca fecha cedo", TicketStatus.OPEN),
                                createTicket(userIds[8], "App bug", "Reserva não salva", TicketStatus.IN_PROGRESS),
                                createTicket(userIds[9], "Pagamento multa", "Como pagar?", TicketStatus.CLOSED,
                                                "Link PagSeguro"),
                                createTicket(adminId, "Teste ticket", "Funciona?", TicketStatus.CLOSED, "Sim")
                };
                supportTicketRepository.saveAll(Arrays.asList(tickets));

                System.out.println("-----------------------------------------");
                System.out.println("✅ MASSIVE DADOS CARREGADOS: " + savedUsers.size() + " users, " + savedBooks.size()
                                + " books, loans/res/tickets!");
                System.out.println("Admin: admin@library.com / admin123");
                System.out.println("All readers: user123");
                System.out.println("-----------------------------------------");
        }

        private Livro createBook(String titulo, String autor, String editora, int ano, String localizacao, String isbn,
                        int total, int disponiveis) {
                Livro b = new Livro();
                b.setTitulo(titulo);
                b.setAutor(autor);
                b.setEditora(editora);
                b.setAnoPublicacao(ano);
                b.setLocalizacaoFisica(localizacao);
                b.setIsbn(isbn);
                b.setTotalExemplares(total);
                b.setExemplaresDisponiveis(disponiveis);
                return b;
        }

        private Emprestimo createLoan(String leitorId, String bookId, LocalDateTime emprestimo, LocalDateTime vencimento,
                        LoanStatus status) {
                return createLoan(leitorId, bookId, emprestimo, vencimento, status, 0.0);
        }

        private Emprestimo createLoan(String leitorId, String bookId, LocalDateTime emprestimo, LocalDateTime vencimento,
                        LoanStatus status, double multa) {
                Emprestimo l = new Emprestimo();
                l.setLeitorId(leitorId);
                l.setBookId(bookId);
                l.setDataEmprestimo(emprestimo);
                l.setDataVencimento(vencimento);
                l.setStatus(status);
                l.setMultaCalculada(multa);
                return l;
        }

        private Reserva createReservation(String bookId, String leitorId, int posicao) {
                Reserva r = new Reserva();
                r.setBookId(bookId);
                r.setLeitorId(leitorId);
                r.setDataSolicitacao(LocalDateTime.now().minusHours((long) posicao * 2));
                r.setPosicaoNaFila(posicao);
                r.setAtiva(true);
                return r;
        }

        private SupportTicket createTicket(String leitorId, String assunto, String mensagem, TicketStatus status) {
                return createTicket(leitorId, assunto, mensagem, status, null);
        }

        private SupportTicket createTicket(String leitorId, String assunto, String mensagem, TicketStatus status,
                        String resposta) {
                SupportTicket t = new SupportTicket();
                t.setLeitorId(leitorId);
                t.setAssunto(assunto);
                t.setMensagem(mensagem);
                t.setDataRegistro(LocalDateTime.now().minusDays((long) (Math.random() * 30)));
                t.setStatus(status);
                t.setRespostaBibliotecario(resposta);
                return t;
        }
}
