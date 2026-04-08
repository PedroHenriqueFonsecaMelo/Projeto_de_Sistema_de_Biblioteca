package br.umc.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.umc.demo.entity.LoanStatus;
import br.umc.demo.repository.LoanRepository;
import br.umc.demo.repository.UserRepository;

@Controller
public class DashboardController {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalLoans", loanRepository.count());
        model.addAttribute("activeUsers", userRepository.count());
        model.addAttribute("overdueBooks", loanRepository.countByStatus(LoanStatus.OVERDUE));
        return "Dashboard";
    }

}