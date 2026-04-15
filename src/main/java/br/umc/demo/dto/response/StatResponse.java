package br.umc.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatResponse {

    private Long totalUsers;
    private Long totalBooks;
    private Long activeLoans;
    private Long atrasadoLoans;
    private Long pendingFines;
    private List<Map<String, Object>> popularBooks;

    public StatResponse(long totalBooks, long totalUsers, long pendingFines, long atrasadoLoans,
            List<Map<String, Object>> popularBooks) {
        this.totalBooks = totalBooks;
        this.totalUsers = totalUsers;
        this.pendingFines = pendingFines;
        this.atrasadoLoans = atrasadoLoans;
        this.popularBooks = popularBooks;
        this.activeLoans = 0L;
    }
}