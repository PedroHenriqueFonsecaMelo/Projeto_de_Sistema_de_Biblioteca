package br.umc.demo.controller.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @GetMapping("/summary")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        

        stats.put("totalBooks", 1234);
        stats.put("activeLoans", 456);
        stats.put("overdue", 56);
        stats.put("activeUsers", 89);


        List<Map<String, Object>> popular = Arrays.asList(
            createBookStat("O Senhor dos Anéis", 245, 92),
            createBookStat("Dom Casmurro", 189, 75),
            createBookStat("1984", 156, 60)
        );
        stats.put("popularBooks", popular);

        return stats;
    }

    private Map<String, Object> createBookStat(String title, int count, int pct) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("count", count);
        map.put("percentage", pct);
        return map;
    }
}