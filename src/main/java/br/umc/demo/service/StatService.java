package br.umc.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.umc.demo.entity.Livro;
import br.umc.demo.entity.Emprestimo;
import br.umc.demo.entity.enums.EmprestimoStatus;
import br.umc.demo.repository.LivroRepository;
import br.umc.demo.repository.EmprestimoRepository;
import br.umc.demo.repository.UserRepository;

@Service
public class StatService {

        @Autowired
        private EmprestimoRepository loanRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private LivroRepository bookRepository;

        @SuppressWarnings("null")
        public Map<String, Object> getLibraryReport() {

                long totalBooks = bookRepository.count();
                long totalUsers = userRepository.count();

                long atrasado = loanRepository.countByStatusAndDataVencimentoBefore(
                                EmprestimoStatus.ATIVO,
                                LocalDateTime.now());

                long totalFines = loanRepository.countByStatus(EmprestimoStatus.ATRASADO) * 2;

                List<Emprestimo> returnedLoans = loanRepository
                                .findFirst10ByStatusOrderByDataEmprestimoDesc(EmprestimoStatus.RETORNADO);

                Map<String, Long> bookCountsMap = new HashMap<>();
                for (Emprestimo loan : returnedLoans) {
                        String bookId = loan.getBookId();
                        Long currentCount = bookCountsMap.getOrDefault(bookId, 0L);
                        bookCountsMap.put(bookId, currentCount + 1);
                }

                List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(bookCountsMap.entrySet());
                sortedEntries.sort(new Comparator<Map.Entry<String, Long>>() {
                        public int compare(Map.Entry<String, Long> e1, Map.Entry<String, Long> e2) {
                                return (int) (e2.getValue() - e1.getValue());
                        }
                });
                List<Map<String, Object>> popular = new ArrayList<>();
                for (int i = 0; i < Math.min(5, sortedEntries.size()); i++) {

                        Map.Entry<String, Long> entry = sortedEntries.get(i);
                        if (entry.getValue() == 0) {
                                continue;
                        }

                        Optional<Livro> optB = bookRepository.findById(entry.getKey());
                        Livro b = null;
                        if (optB.isPresent()) {
                                b = optB.get();
                        }

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
                data.put("activeLoans", loanRepository.countByStatus(EmprestimoStatus.ATIVO));
                data.put("atrasado", atrasado);
                data.put("activeUsers", totalUsers);
                data.put("totalFines", totalFines);
                data.put("popularBooks", popular);

                return data;
        }
}
