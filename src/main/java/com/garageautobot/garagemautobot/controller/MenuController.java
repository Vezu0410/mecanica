package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.services.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final VeiculoService veiculoService;

    @Autowired
    public MenuController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public String exibirMenu(Model model) {
        // Aqui você já pode buscar os carros em manutenção ou aguardando peça
        model.addAttribute("veiculos", veiculoService.findByStatusEmManutencaoOuAguardando());
        return "menu"; // chama o menu.html
    }
}
