package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.services.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RelatorioPecaController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping("/relatorios/pecas-por-veiculo")
    public String relatorioPecasPorVeiculo(Model model) {
        model.addAttribute("veiculos", veiculoService.findAll());
        return "relatorio-pecas-veiculo";
    }
}
