package br.umc.demo.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import br.umc.demo.service.StatService;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("hasRole('LIBRARIAN')")
public class StatsControler {

    @Autowired
    private StatService reportService;

    @GetMapping("/summary")
    public Map<String, Object> getDashboardStats() {
        return reportService.getLibraryReport();
    }
    
}
