package br.umc.demo.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import br.umc.demo.service.ReportService;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/summary")
public Map<String, Object> getDashboardStats() {
        return reportService.getLibraryReport();
    }
}
