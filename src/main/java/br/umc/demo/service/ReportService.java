package br.umc.demo.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.umc.demo.dto.ReportResponse;
import br.umc.demo.entity.LoanStatus; // Importe seu Enum
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


        List<Map<String, Object>> popular = Arrays.asList(
            Map.of("title", "O Senhor dos Anéis", "count", 245, "percentage", 92),
            Map.of("title", "1984", "count", 156, "percentage", 65),
            Map.of("title", "Dom Casmurro", "count", 120, "percentage", 45)
        );


        return new ReportResponse(
            totalBooks,
            totalUsers,
            0L,      // multas pendentes (exemplo)
            overdue,
            popular
        );
    }
}