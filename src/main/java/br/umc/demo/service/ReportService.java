package br.umc.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.umc.demo.entity.Book;
import br.umc.demo.entity.Loan;
import br.umc.demo.entity.LoanStatus;
import br.umc.demo.repository.BookRepository;
import br.umc.demo.repository.LoanRepository;
import br.umc.demo.repository.UserRepository;

@Service
public class ReportService {

        @Autowired
        private LoanRepository loanRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BookRepository bookRepository;

        public Map<String, Object> getLibraryReport() {

                long totalBooks = bookRepository.count();
                long totalUsers = userRepository.count();

                long overdue = loanRepository.countByStatusAndDataVencimentoBefore(
                                LoanStatus.ACTIVE,
                                LocalDateTime.now());

                long totalFines = loanRepository.countByStatus(LoanStatus.OVERDUE) * 2;

                // Real top 5 popular books by loan count (recent returned loans) - no lambdas
                List<Loan> returnedLoans = loanRepository
                                .findFirst10ByStatusOrderByDataEmprestimoDesc(LoanStatus.RETURNED);

                // Group by bookId count
                Map<String, Long> bookCountsMap = new HashMap<>();
                for (Loan loan : returnedLoans) {
                        String bookId = loan.getBookId();
                        Long currentCount = bookCountsMap.getOrDefault(bookId, 0L);
                        bookCountsMap.put(bookId, currentCount + 1);
                }

                List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(bookCountsMap.entrySet());
                sortedEntries.sort((e1, e2) -> (int) (e2.getValue() - e1.getValue()));
                List<Map<String, Object>> popular = new ArrayList<>();
                for (int i = 0; i < Math.min(5, sortedEntries.size()); i++) {

                        Map.Entry<String, Long> entry = sortedEntries.get(i);
                        if (entry.getValue() == 0) {
                                continue;
                        }

                        
                        @SuppressWarnings("null")
                        Book b = bookRepository.findById(entry.getKey()).orElse(null);

                        if (b != null) {
                                Map<String, Object> item = new HashMap<>();
                                item.put("title", b.getTitulo());
                                item.put("count", entry.getValue());
                                item.put("percentage", (int) (entry.getValue() * 20));
                                popular.add(item);
                        }
                }

                Map<String, Object> data = new HashMap<>();
                data.put("totalBooks", totalBooks);
                data.put("activeLoans", loanRepository.countByStatus(LoanStatus.ACTIVE));
                data.put("overdue", overdue);
                data.put("activeUsers", totalUsers);
                data.put("totalFines", totalFines);
                data.put("popularBooks", popular);

                return data;
        }
}
