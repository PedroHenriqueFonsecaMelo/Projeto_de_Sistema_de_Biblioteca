package br.umc.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import br.umc.demo.entity.SupportTicket;
import br.umc.demo.repository.UserRepository;
import br.umc.demo.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Controller
public class ViewController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "index";
    }
}
