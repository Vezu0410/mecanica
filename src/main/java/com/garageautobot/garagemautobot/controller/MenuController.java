package com.garageautobot.garagemautobot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.garageautobot.garagemautobot.entities.StatusVeiculo;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.services.VeiculoService;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final VeiculoService veiculoService;

    @Autowired
    public MenuController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public String exibirMenu(
            Model model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search // novo parâmetro
    ) {

        List<Veiculo> veiculos;

        if (search != null && !search.isEmpty()) {
            veiculos = veiculoService.search(search); // busca por marca, modelo ou cliente
        } else if (status == null || status.equals("TODOS")) {
            veiculos = veiculoService.findAll();
        } else {
            StatusVeiculo statusEnum = StatusVeiculo.valueOf(status);
            veiculos = veiculoService.findByStatus(statusEnum);
        }

        model.addAttribute("veiculos", veiculos);
        model.addAttribute("status", status);
        model.addAttribute("search", search); // mantém o termo no input
        return "menu";
    }


}
