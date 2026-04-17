package br.umc.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import br.umc.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
