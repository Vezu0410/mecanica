package com.garageautobot.garagemautobot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.garageautobot.garagemautobot.entities.Peca;
import com.garageautobot.garagemautobot.entities.StatusVeiculo;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.PecaRepository;
import com.garageautobot.garagemautobot.services.PecaService;
import com.garageautobot.garagemautobot.services.VeiculoService;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final VeiculoService veiculoService;
    
    @Autowired
    private PecaService pecaService;
    
    @Autowired
    private PecaRepository pecaRepository;
    @Autowired
    public MenuController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping("/menu")
    public String menu(Model model, @RequestParam(required = false) String status) {
        model.addAttribute("veiculos", veiculoService.buscarPorStatus(status));
        model.addAttribute("pecas", pecaRepository.findAll());
        return "menu";
    }
    @GetMapping
    public String exibirMenu(
            Model model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {

        List<Veiculo> veiculos;

        if (search != null && !search.isEmpty()) {
            veiculos = veiculoService.search(search);
        } else if (status == null || status.equals("TODOS")) {
            veiculos = veiculoService.findAll();
        } else {
            StatusVeiculo statusEnum = StatusVeiculo.valueOf(status);
            veiculos = veiculoService.findByStatus(statusEnum);
        }
        
        List<Peca> pecas = pecaService.findAll();

        model.addAttribute("veiculos", veiculos);
        model.addAttribute("status", status);
        model.addAttribute("pecas", pecas);
        model.addAttribute("search", search);
        return "menu";
    }
}
