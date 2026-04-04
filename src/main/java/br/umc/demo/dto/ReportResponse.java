package br.umc.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long totalUsers;
    private Long totalBooks;
    private Long activeLoans;
    private Long overdueLoans;
    private Long pendingFines; // Adicionado para bater com o seu construtor
    private List<Map<String, Object>> popularBooks;

    public ReportResponse(long totalBooks, long totalUsers, long pendingFines, long overdueLoans, List<Map<String, Object>> popularBooks) {
        this.totalBooks = totalBooks;
        this.totalUsers = totalUsers;
        this.pendingFines = pendingFines;
        this.overdueLoans = overdueLoans;
        this.popularBooks = popularBooks;
        this.activeLoans = 0L;
    }
}