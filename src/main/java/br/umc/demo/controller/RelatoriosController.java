package br.umc.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {

    @GetMapping
    public String paginaRelatorios() {
        return "relatorios";
    }
}
