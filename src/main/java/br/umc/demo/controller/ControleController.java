package br.umc.demo.controller;

import br.umc.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/controle")
public class ControleController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String paginaControle(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "Controle";
    }
}
