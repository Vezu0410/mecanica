package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Peca;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.services.PecaService;
import com.garageautobot.garagemautobot.services.VeiculoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pecas")
public class PecaController {

    private final PecaService pecaService;


    @Autowired
    private VeiculoService veiculoService;
    
    @Autowired
    public PecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    // Exibir formulário de cadastro
    @GetMapping("/cadastro")
    public String exibirFormulaarioCadastro(Model model) {
        model.addAttribute("peca", new Peca());
        return "cadastro-peca"; // HTML do cadastro
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Peca peca) {
        pecaService.save(peca);
        return "redirect:/pecas/lista";
    }

    // Deletar peça
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        pecaService.delete(id);
        return "redirect:/pecas/lista";
    }
    
    @GetMapping
    public String listarMenu(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model) {

        List<Veiculo> veiculos;

        if (search != null && !search.isEmpty()) {
            veiculos = veiculoService.search(search);
        } else {
            veiculos = veiculoService.buscarPorStatus(status);
        }

        List<Peca> pecas = pecaService.findAll();

        model.addAttribute("veiculos", veiculos);
        model.addAttribute("pecas", pecas);
        model.addAttribute("status", status);
        model.addAttribute("search", search);

        return "menu";
    }

}
