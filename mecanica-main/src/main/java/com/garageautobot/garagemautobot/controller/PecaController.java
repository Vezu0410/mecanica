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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "cadastro-peca";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Peca peca) {
        pecaService.save(peca);
        return "redirect:/pecas/lista";
    }

    // INATIVAR peça (soft delete) - antes era "deletar"
    @PostMapping("/inativar/{id}")
    public String inativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            pecaService.inativar(id);
            ra.addFlashAttribute("sucesso", "Peça inativada. Ela não aparece mais nas listas, mas o histórico foi preservado.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao inativar peça: " + e.getMessage());
        }
        return "redirect:/pecas/lista";
    }

    // REATIVAR peça (somente admin - protegido no WebConfig)
    @PostMapping("/reativar/{id}")
    public String reativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            pecaService.reativar(id);
            ra.addFlashAttribute("sucesso", "Peça reativada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao reativar peça: " + e.getMessage());
        }
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