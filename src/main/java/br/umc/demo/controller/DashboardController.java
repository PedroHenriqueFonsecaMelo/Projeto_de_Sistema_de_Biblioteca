package br.umc.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalLoans", "2.840");
        model.addAttribute("activeUsers", "1.120");
        model.addAttribute("overdueBooks", "12");
        return "Dashboard";
    }

}