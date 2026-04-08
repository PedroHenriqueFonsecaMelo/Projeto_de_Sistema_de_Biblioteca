package br.umc.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.umc.demo.dto.ReportResponse;
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

    public ReportResponse getLibraryReport() {

        long totalBooks = bookRepository.count();
        long totalUsers = userRepository.count();

        long overdue = loanRepository.countByStatusAndDataVencimentoBefore(
            LoanStatus.ACTIVE,
            LocalDateTime.now()
        );

        // Sample popular from massive data (in prod: Mongo aggregate $group by bookId from loans)
        List<Map<String, Object>> popular = List.of(
            Map.of("title", "Clean Code", "count", 12, "percentage", 80),
            Map.of("title", "O Senhor dos Anéis", "count", 10, "percentage", 70),
            Map.of("title", "Java: Como Programar", "count", 8, "percentage", 60),
            Map.of("title", "1984", "count", 5, "percentage", 40),
            Map.of("title", "Dom Casmurro", "count", 4, "percentage", 30)
        );

        return new ReportResponse(totalBooks, totalUsers, (long) (loanRepository.countByStatus(LoanStatus.OVERDUE) * 2.0), overdue, popular);
    }
}

